package top.zhjh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import top.zhjh.config.dict.SystemDictDefinition;
import top.zhjh.config.dict.SystemDictItemDefinition;
import top.zhjh.config.dict.SystemDictRegistry;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.enums.DictSourceScope;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDictItemMapper;
import top.zhjh.mapper.SysDictMapper;
import top.zhjh.mapper.SysTenantMapper;
import top.zhjh.model.entity.SysDict;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.entity.SysTenant;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 系统字典同步服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemDictSyncService {

  private final JdbcTemplate jdbcTemplate;
  private final SystemDictRegistry systemDictRegistry;
  private final SysDictMapper sysDictMapper;
  private final SysDictItemMapper sysDictItemMapper;
  private final SysTenantMapper sysTenantMapper;
  private final TransactionTemplate transactionTemplate;

  public void syncOnStartup() {
    try {
      if (!dictTablesExist()) {
        log.warn("系统字典同步已跳过：sys_dict 或 sys_dict_item 表不存在");
        return;
      }
      List<String> missingColumns = missingRequiredColumns();
      if (!missingColumns.isEmpty()) {
        log.warn("系统字典同步已跳过：字典表结构未迁移，缺少字段 [{}]。请先执行 sql/20260406_dict.sql 中的迁移段",
          String.join(", ", missingColumns));
        return;
      }
      TenantContext.runWithoutTenant(() -> transactionTemplate.executeWithoutResult(status -> syncAll()));
    } catch (Exception e) {
      log.warn("系统字典同步已跳过：{}", e.getMessage());
      log.debug("系统字典同步跳过详情", e);
    }
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public void initTenantDictionaries(Long tenantId) {
    if (tenantId == null) {
      throw new ServiceException("租户ID不能为空，无法初始化系统字典");
    }
    ensureDictSchemaReady();
    TenantContext.runWithoutTenant(() -> {
      SyncStats stats = syncTenantDefinitions(tenantId);
      log.info("租户系统字典初始化完成：tenantId={}，新增字典{}个，新增字典项{}个，发现漂移{}处",
        tenantId, stats.createdDicts, stats.createdItems, stats.driftWarnings);
    });
  }

  private boolean dictTablesExist() {
    Integer dictCount = jdbcTemplate.queryForObject(
      "select count(*) from information_schema.tables where table_schema = database() and table_name = 'sys_dict'",
      Integer.class
    );
    Integer itemCount = jdbcTemplate.queryForObject(
      "select count(*) from information_schema.tables where table_schema = database() and table_name = 'sys_dict_item'",
      Integer.class
    );
    return Integer.valueOf(1).equals(dictCount) && Integer.valueOf(1).equals(itemCount);
  }

  private List<String> missingRequiredColumns() {
    List<String> requiredColumns = List.of(
      "sys_dict.system_built_in",
      "sys_dict.code_referenced",
      "sys_dict.allow_tenant_override",
      "sys_dict.source_scope",
      "sys_dict.base_dict_id",
      "sys_dict_item.system_built_in",
      "sys_dict_item.code_referenced",
      "sys_dict_item.base_item_id"
    );
    String sql = "select concat(table_name, '.', column_name) "
      + "from information_schema.columns "
      + "where table_schema = database() "
      + "and ("
      + "  ("
      + "    table_name = 'sys_dict' "
      + "    and column_name in ('system_built_in', 'code_referenced', 'allow_tenant_override', 'source_scope', 'base_dict_id')"
      + "  )"
      + "  or ("
      + "    table_name = 'sys_dict_item' "
      + "    and column_name in ('system_built_in', 'code_referenced', 'base_item_id')"
      + "  )"
      + ")";
    List<String> existingColumns = jdbcTemplate.queryForList(sql, String.class);
    Set<String> existingColumnSet = new HashSet<>(existingColumns);
    return requiredColumns.stream()
      .filter(column -> !existingColumnSet.contains(column))
      .toList();
  }

  private void ensureDictSchemaReady() {
    if (!dictTablesExist()) {
      throw new ServiceException("字典表不存在，无法初始化租户系统字典");
    }
    List<String> missingColumns = missingRequiredColumns();
    if (!missingColumns.isEmpty()) {
      throw new ServiceException("字典表结构未迁移，无法初始化租户系统字典，缺少字段：" + String.join(", ", missingColumns));
    }
  }

  private void syncAll() {
    int createdDicts = 0;
    int createdItems = 0;
    int driftWarnings = 0;
    List<SysTenant> tenants = sysTenantMapper.selectList(new LambdaQueryWrapper<SysTenant>()
      .select(SysTenant::getId));

    for (SystemDictDefinition definition : systemDictRegistry.list()) {
      SyncStats publicStats = syncDefinition(definition, 0L, null, DictSourceScope.PUBLIC_SYSTEM);
      createdDicts += publicStats.createdDicts;
      createdItems += publicStats.createdItems;
      driftWarnings += publicStats.driftWarnings;

      if (!definition.isAllowTenantOverride()) {
        continue;
      }
      for (SysTenant tenant : tenants) {
        SyncStats tenantStats = syncDefinition(definition, tenant.getId(), publicStats.syncedDict, DictSourceScope.TENANT_OVERRIDE);
        createdDicts += tenantStats.createdDicts;
        createdItems += tenantStats.createdItems;
        driftWarnings += tenantStats.driftWarnings;
      }
    }

    log.info("系统字典同步完成：新增字典{}个，新增字典项{}个，发现漂移{}处", createdDicts, createdItems, driftWarnings);
  }

  private SyncStats syncTenantDefinitions(Long tenantId) {
    SyncStats stats = new SyncStats();
    for (SystemDictDefinition definition : systemDictRegistry.list()) {
      SyncStats publicStats = syncDefinition(definition, 0L, null, DictSourceScope.PUBLIC_SYSTEM);
      stats.merge(publicStats);

      if (!definition.isAllowTenantOverride()) {
        continue;
      }
      SyncStats tenantStats = syncDefinition(definition, tenantId, publicStats.syncedDict, DictSourceScope.TENANT_OVERRIDE);
      stats.merge(tenantStats);
    }
    return stats;
  }

  private SyncStats syncDefinition(SystemDictDefinition definition, Long tenantId, SysDict baseDict, DictSourceScope sourceScope) {
    SyncStats stats = new SyncStats();
    SysDict dict = findDict(definition.getDictKey(), tenantId, sourceScope, baseDict == null ? null : baseDict.getId())
      .orElseGet(() -> {
        SysDict created = new SysDict();
        created.setName(definition.getDefaultName());
        created.setKey(definition.getDictKey());
        created.setRemarks(definition.getDefaultRemarks());
        created.setTenantId(tenantId);
        created.setSystemBuiltIn(definition.isSystemBuiltIn());
        created.setCodeReferenced(definition.isCodeReferenced());
        created.setAllowTenantOverride(definition.isAllowTenantOverride());
        created.setSourceScope(sourceScope);
        created.setBaseDictId(baseDict == null ? null : baseDict.getId());
        sysDictMapper.insert(created);
        stats.createdDicts++;
        return created;
      });

    boolean dictNeedUpdate = false;
    if (!Objects.equals(dict.getSystemBuiltIn(), definition.isSystemBuiltIn())) {
      dict.setSystemBuiltIn(definition.isSystemBuiltIn());
      dictNeedUpdate = true;
    }
    if (!Objects.equals(dict.getCodeReferenced(), definition.isCodeReferenced())) {
      dict.setCodeReferenced(definition.isCodeReferenced());
      dictNeedUpdate = true;
    }
    if (!Objects.equals(dict.getAllowTenantOverride(), definition.isAllowTenantOverride())) {
      dict.setAllowTenantOverride(definition.isAllowTenantOverride());
      dictNeedUpdate = true;
    }
    if (!Objects.equals(dict.getSourceScope(), sourceScope)) {
      dict.setSourceScope(sourceScope);
      dictNeedUpdate = true;
    }
    Long baseDictId = baseDict == null ? null : baseDict.getId();
    if (!Objects.equals(dict.getBaseDictId(), baseDictId)) {
      dict.setBaseDictId(baseDictId);
      dictNeedUpdate = true;
    }
    if (!Objects.equals(dict.getKey(), definition.getDictKey())) {
      dict.setKey(definition.getDictKey());
      dictNeedUpdate = true;
    }
    if (dictNeedUpdate) {
      sysDictMapper.updateById(dict);
    }
    if (!Objects.equals(dict.getName(), definition.getDefaultName()) || !Objects.equals(dict.getRemarks(), definition.getDefaultRemarks())) {
      stats.driftWarnings++;
      log.warn("系统字典显示字段漂移：key={}, tenantId={}, dbName={}, codeName={}",
        definition.getDictKey(), tenantId, dict.getName(), definition.getDefaultName());
    }

    for (SystemDictItemDefinition itemDefinition : definition.getItems()) {
      SysDictItem baseItem = null;
      if (baseDict != null) {
        baseItem = findItem(baseDict.getId(), itemDefinition.getItemValue()).orElse(null);
      }
      SyncStats itemStats = syncItem(dict, itemDefinition, baseItem);
      stats.createdItems += itemStats.createdItems;
      stats.driftWarnings += itemStats.driftWarnings;
    }

    long extraItemCount = sysDictItemMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
      .eq(SysDictItem::getDictId, dict.getId())
      .notIn(SysDictItem::getItemValue, definition.getItems().stream().map(SystemDictItemDefinition::getItemValue).toList()));
    if (extraItemCount > 0) {
      stats.driftWarnings++;
      log.warn("系统字典存在额外字典项：key={}, tenantId={}, count={}", definition.getDictKey(), tenantId, extraItemCount);
    }
    stats.syncedDict = dict;
    return stats;
  }

  private SyncStats syncItem(SysDict dict, SystemDictItemDefinition definition, SysDictItem baseItem) {
    SyncStats stats = new SyncStats();
    SysDictItem item = findItem(dict.getId(), definition.getItemValue())
      .orElseGet(() -> {
        SysDictItem created = new SysDictItem();
        created.setDictId(dict.getId());
        created.setDictKey(dict.getKey());
        created.setTenantId(dict.getTenantId());
        created.setItemValue(definition.getItemValue());
        created.setLabel(definition.getDefaultLabel());
        created.setDescription(definition.getDefaultDescription());
        created.setSortOrder(definition.getDefaultSortOrder());
        created.setSystemBuiltIn(definition.isSystemBuiltIn());
        created.setCodeReferenced(definition.isCodeReferenced());
        created.setBaseItemId(baseItem == null ? null : baseItem.getId());
        sysDictItemMapper.insert(created);
        stats.createdItems++;
        return created;
      });

    boolean needUpdate = false;
    if (!Objects.equals(item.getDictId(), dict.getId())) {
      item.setDictId(dict.getId());
      needUpdate = true;
    }
    if (!Objects.equals(item.getDictKey(), dict.getKey())) {
      item.setDictKey(dict.getKey());
      needUpdate = true;
    }
    if (!Objects.equals(item.getTenantId(), dict.getTenantId())) {
      item.setTenantId(dict.getTenantId());
      needUpdate = true;
    }
    if (!Objects.equals(item.getItemValue(), definition.getItemValue())) {
      item.setItemValue(definition.getItemValue());
      needUpdate = true;
    }
    if (!Objects.equals(item.getSystemBuiltIn(), definition.isSystemBuiltIn())) {
      item.setSystemBuiltIn(definition.isSystemBuiltIn());
      needUpdate = true;
    }
    if (!Objects.equals(item.getCodeReferenced(), definition.isCodeReferenced())) {
      item.setCodeReferenced(definition.isCodeReferenced());
      needUpdate = true;
    }
    Long baseItemId = baseItem == null ? null : baseItem.getId();
    if (!Objects.equals(item.getBaseItemId(), baseItemId)) {
      item.setBaseItemId(baseItemId);
      needUpdate = true;
    }
    if (needUpdate) {
      sysDictItemMapper.updateById(item);
    }
    if (!Objects.equals(item.getLabel(), definition.getDefaultLabel())
      || !Objects.equals(item.getDescription(), definition.getDefaultDescription())
      || !Objects.equals(item.getSortOrder(), definition.getDefaultSortOrder())) {
      stats.driftWarnings++;
      log.warn("系统字典项显示字段漂移：dictKey={}, itemValue={}, dbLabel={}, codeLabel={}",
        dict.getKey(), definition.getItemValue(), item.getLabel(), definition.getDefaultLabel());
    }
    return stats;
  }

  private Optional<SysDict> findDict(String dictKey, Long tenantId, DictSourceScope sourceScope, Long baseDictId) {
    SysDict exactDict = sysDictMapper.selectOne(new LambdaQueryWrapper<SysDict>()
      .eq(SysDict::getKey, dictKey)
      .eq(SysDict::getTenantId, tenantId)
      .eq(SysDict::getSourceScope, sourceScope)
      .last("limit 1"));
    if (exactDict != null) {
      return Optional.of(exactDict);
    }

    SysDict driftedDict = sysDictMapper.selectOne(new LambdaQueryWrapper<SysDict>()
      .eq(SysDict::getKey, dictKey)
      .eq(SysDict::getTenantId, tenantId)
      .last("limit 1"));
    if (driftedDict == null) {
      return Optional.empty();
    }
    if (isSystemDictCandidate(driftedDict, sourceScope, baseDictId)) {
      return Optional.of(driftedDict);
    }
    throw new ServiceException("字典标识已被非系统字典占用，无法同步系统字典：" + dictKey);
  }

  private boolean isSystemDictCandidate(SysDict dict, DictSourceScope sourceScope, Long baseDictId) {
    return Boolean.TRUE.equals(dict.getSystemBuiltIn())
      || Boolean.TRUE.equals(dict.getCodeReferenced())
      || Objects.equals(dict.getBaseDictId(), baseDictId)
      || (DictSourceScope.PUBLIC_SYSTEM.equals(sourceScope)
      && Long.valueOf(0L).equals(dict.getTenantId())
      && dict.getBaseDictId() == null);
  }

  private Optional<SysDictItem> findItem(Long dictId, String itemValue) {
    return Optional.ofNullable(sysDictItemMapper.selectOne(new LambdaQueryWrapper<SysDictItem>()
      .eq(SysDictItem::getDictId, dictId)
      .eq(SysDictItem::getItemValue, itemValue)
      .last("limit 1")));
  }

  private static class SyncStats {
    private int createdDicts;
    private int createdItems;
    private int driftWarnings;
    private SysDict syncedDict;

    private void merge(SyncStats other) {
      this.createdDicts += other.createdDicts;
      this.createdItems += other.createdItems;
      this.driftWarnings += other.driftWarnings;
    }
  }
}
