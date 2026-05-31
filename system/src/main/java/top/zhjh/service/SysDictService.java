package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zhjh.base.model.PageVO;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.enums.DictSourceScope;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDictItemMapper;
import top.zhjh.mapper.SysDictMapper;
import top.zhjh.model.entity.SysDict;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.qo.SysDictListQO;
import top.zhjh.model.qo.SysDictPageQO;
import top.zhjh.model.qo.SysDictRemoveQO;
import top.zhjh.model.qo.SysDictSaveQO;
import top.zhjh.model.qo.SysDictUpdateQO;
import top.zhjh.model.vo.SysDictPageVO;
import top.zhjh.struct.SysDictStruct;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.List;

@Service
public class SysDictService extends ServiceImpl<SysDictMapper, SysDict> {

  @Resource
  private SysDictMapper sysDictMapper;
  @Resource
  private SysDictItemMapper sysDictItemMapper;

  public List<SysDictPageVO> list(SysDictListQO query) {
    return TenantContext.supplyWithoutTenant(() -> {
      fillQueryScope(query);
      return sysDictMapper.list(query);
    });
  }

  public PageVO<SysDictPageVO> page(SysDictPageQO query) {
    return TenantContext.supplyWithoutTenant(() -> {
      fillQueryScope(query);
      List<SysDictPageVO> records = sysDictMapper.page(query);
      return new PageVO<SysDictPageVO>(query).setRecords(records);
    });
  }

  public SysDict get(Long id) {
    return TenantContext.supplyWithoutTenant(() -> getAccessibleDict(id));
  }

  public SysDict resolveEffectiveDict(String dictKey, Long tenantId) {
    return TenantContext.supplyWithoutTenant(() -> resolveEffectiveDictInternal(dictKey, tenantId));
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysDictSaveQO obj) {
    return TenantContext.supplyWithoutTenant(() -> {
      Long tenantId = currentTenantIdForWrite();
      if (this.lambdaQuery()
        .eq(SysDict::getKey, obj.getKey())
        .eq(SysDict::getTenantId, tenantId)
        .count() > 0) {
        throw new ServiceException("字典标识已存在");
      }
      SysDict sysDict = SysDictStruct.INSTANCE.to(obj);
      sysDict.setTenantId(tenantId);
      sysDict.setSourceScope(DictSourceScope.TENANT_CUSTOM);
      sysDict.setSystemBuiltIn(false);
      sysDict.setCodeReferenced(false);
      sysDict.setAllowTenantOverride(false);
      return this.save(sysDict);
    });
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysDictUpdateQO obj) {
    return TenantContext.supplyWithoutTenant(() -> {
      SysDict exist = getAccessibleDict(obj.getId());
      validateUpdatableDict(exist);
      SysDict sysDict = SysDictStruct.INSTANCE.to(obj);
      sysDict.setKey(exist.getKey());
      return this.updateById(sysDict);
    });
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysDictRemoveQO query) {
    return TenantContext.supplyWithoutTenant(() -> {
      List<SysDict> dictList = this.lambdaQuery()
        .in(SysDict::getId, query.getIds())
        .list();
      if (dictList.size() != query.getIds().size()) {
        throw new ServiceException("字典不存在");
      }
      for (SysDict dict : dictList) {
        validateRemovableDict(dict);
      }
      sysDictItemMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysDictItem>()
        .in(SysDictItem::getDictId, query.getIds()));
      return this.removeByIds(query.getIds());
    });
  }

  private void fillQueryScope(SysDictListQO query) {
    query.setTenantId(currentTenantIdForRead());
    query.setSuperAdmin(isSuperAdmin());
    if (query.getEffectiveOnly() == null) {
      query.setEffectiveOnly(true);
    }
  }

  private void fillQueryScope(SysDictPageQO query) {
    query.setTenantId(currentTenantIdForRead());
    query.setSuperAdmin(isSuperAdmin());
    if (query.getEffectiveOnly() == null) {
      query.setEffectiveOnly(true);
    }
  }

  private SysDict getAccessibleDict(Long id) {
    SysDict sysDict = this.lambdaQuery()
      .eq(SysDict::getId, id)
      .one();
    if (sysDict == null) {
      throw new ServiceException("字典不存在");
    }
    if (!isSuperAdmin()) {
      Long tenantId = currentTenantIdForRead();
      if (!tenantId.equals(sysDict.getTenantId())) {
        throw new ServiceException("字典不存在");
      }
    }
    return sysDict;
  }

  private void validateUpdatableDict(SysDict dict) {
    if (!isSuperAdmin() && !currentTenantIdForWrite().equals(dict.getTenantId())) {
      throw new ServiceException("无权修改当前字典");
    }
    if (!isSuperAdmin() && DictSourceScope.PUBLIC_SYSTEM.equals(dict.getSourceScope())) {
      throw new ServiceException("无权修改系统公共字典");
    }
  }

  private void validateRemovableDict(SysDict dict) {
    if (Boolean.TRUE.equals(dict.getCodeReferenced())) {
      throw new ServiceException("系统引用字典不允许删除");
    }
    if (!isSuperAdmin() && !currentTenantIdForWrite().equals(dict.getTenantId())) {
      throw new ServiceException("无权删除当前字典");
    }
  }

  private SysDict resolveEffectiveDictInternal(String dictKey, Long tenantId) {
    Long currentTenantId = tenantId;
    if (currentTenantId == null) {
      currentTenantId = currentTenantIdForRead();
    }
    Optional<SysDict> tenantDict = findByKeyAndTenant(dictKey, currentTenantId);
    if (tenantDict.isPresent()) {
      return tenantDict.get();
    }
    if (Long.valueOf(0L).equals(currentTenantId)) {
      throw new ServiceException("字典不存在");
    }
    return findByKeyAndTenant(dictKey, 0L)
      .orElseThrow(() -> new ServiceException("字典不存在"));
  }

  private Optional<SysDict> findByKeyAndTenant(String dictKey, Long tenantId) {
    return Optional.ofNullable(this.lambdaQuery()
      .eq(SysDict::getKey, dictKey)
      .eq(SysDict::getTenantId, tenantId)
      .one());
  }

  private Long currentTenantIdForRead() {
    if (isSuperAdmin()) {
      return 0L;
    }
    Long tenantId = StpExtUtil.getTenantId();
    if (tenantId == null) {
      return 0L;
    }
    return tenantId;
  }

  private Long currentTenantIdForWrite() {
    Long tenantId = StpExtUtil.getTenantId();
    if (tenantId == null) {
      if (isSuperAdmin()) {
        return 0L;
      }
      throw new ServiceException("未指定租户，请重新登录或选择租户");
    }
    return tenantId;
  }

  private boolean isSuperAdmin() {
    return StpExtUtil.isSuperAdmin();
  }
}
