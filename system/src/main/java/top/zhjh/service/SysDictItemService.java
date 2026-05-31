package top.zhjh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zhjh.base.model.PageVO;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDictItemMapper;
import top.zhjh.mapper.SysDictMapper;
import top.zhjh.model.entity.SysDict;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.qo.SysDictItemListQO;
import top.zhjh.model.qo.SysDictItemPageQO;
import top.zhjh.model.qo.SysDictItemRemoveQO;
import top.zhjh.model.qo.SysDictItemSaveQO;
import top.zhjh.model.qo.SysDictItemUpdateQO;
import top.zhjh.model.vo.SysDictItemPageVO;
import top.zhjh.struct.SysDictItemStruct;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class SysDictItemService extends ServiceImpl<SysDictItemMapper, SysDictItem> {

  @Resource
  private SysDictItemMapper sysDictItemMapper;
  @Resource
  private SysDictMapper sysDictMapper;
  @Resource
  private SysDictService sysDictService;

  public List<SysDictItemPageVO> list(SysDictItemListQO query) {
    return TenantContext.supplyWithoutTenant(() -> {
      fillQueryScope(query);
      resolveEffectiveDictQuery(query);
      return sysDictItemMapper.list(query);
    });
  }

  public PageVO<SysDictItemPageVO> page(SysDictItemPageQO query) {
    return TenantContext.supplyWithoutTenant(() -> {
      fillQueryScope(query);
      resolveEffectiveDictQuery(query);
      List<SysDictItemPageVO> records = sysDictItemMapper.page(query);
      return new PageVO<SysDictItemPageVO>(query).setRecords(records);
    });
  }

  public SysDictItem get(Long id) {
    return TenantContext.supplyWithoutTenant(() -> getAccessibleItem(id));
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysDictItemSaveQO obj) {
    return TenantContext.supplyWithoutTenant(() -> {
      SysDict sysDict = getAccessibleDict(obj.getDictId());
      validateUpdatableDict(sysDict);
      validateDuplicate(obj.getDictId(), obj.getItemValue(), obj.getLabel(), null);

      SysDictItem sysDictItem = SysDictItemStruct.INSTANCE.to(obj);
      sysDictItem.setDictKey(sysDict.getKey());
      sysDictItem.setTenantId(sysDict.getTenantId());
      return this.save(sysDictItem);
    });
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysDictItemUpdateQO obj) {
    return TenantContext.supplyWithoutTenant(() -> {
      SysDictItem exist = getAccessibleItem(obj.getId());
      validateUpdatableItem(exist);
      validateItemIdentity(exist, obj);
      SysDict sysDict = getAccessibleDict(obj.getDictId());
      validateUpdatableDict(sysDict);
      validateDuplicate(obj.getDictId(), obj.getItemValue(), obj.getLabel(), obj.getId());

      SysDictItem sysDictItem = SysDictItemStruct.INSTANCE.to(obj);
      sysDictItem.setDictKey(sysDict.getKey());
      sysDictItem.setTenantId(sysDict.getTenantId());
      return this.updateById(sysDictItem);
    });
  }

  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysDictItemRemoveQO query) {
    return TenantContext.supplyWithoutTenant(() -> {
      List<SysDictItem> itemList = this.lambdaQuery()
        .in(SysDictItem::getId, query.getIds())
        .list();
      if (itemList.size() != query.getIds().size()) {
        throw new ServiceException("字典项不存在");
      }
      for (SysDictItem item : itemList) {
        validateRemovableItem(item);
      }
      return this.removeByIds(query.getIds());
    });
  }

  public boolean removeByDictIds(List<Long> dictIds) {
    return TenantContext.supplyWithoutTenant(() -> this.lambdaUpdate()
      .in(SysDictItem::getDictId, dictIds)
      .remove());
  }

  private void fillQueryScope(SysDictItemListQO query) {
    query.setTenantId(currentTenantIdForRead());
    query.setSuperAdmin(isSuperAdmin());
  }

  private void fillQueryScope(SysDictItemPageQO query) {
    query.setTenantId(currentTenantIdForRead());
    query.setSuperAdmin(isSuperAdmin());
  }

  private SysDict getAccessibleDict(Long dictId) {
    SysDict sysDict = sysDictMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysDict>()
      .eq(SysDict::getId, dictId));
    if (sysDict == null) {
      throw new ServiceException("字典不存在");
    }
    if (!isSuperAdmin() && !currentTenantIdForRead().equals(sysDict.getTenantId())) {
      throw new ServiceException("字典不存在");
    }
    return sysDict;
  }

  private SysDictItem getAccessibleItem(Long id) {
    SysDictItem sysDictItem = this.lambdaQuery()
      .eq(SysDictItem::getId, id)
      .one();
    if (sysDictItem == null) {
      throw new ServiceException("字典项不存在");
    }
    if (!isSuperAdmin() && !currentTenantIdForRead().equals(sysDictItem.getTenantId())) {
      throw new ServiceException("字典项不存在");
    }
    return sysDictItem;
  }

  private void validateUpdatableDict(SysDict dict) {
    if (Boolean.TRUE.equals(dict.getCodeReferenced()) && Long.valueOf(0L).equals(dict.getTenantId()) && !isSuperAdmin()) {
      throw new ServiceException("无权修改系统公共字典项");
    }
    if (!isSuperAdmin() && !currentTenantIdForWrite().equals(dict.getTenantId())) {
      throw new ServiceException("无权修改当前字典项");
    }
  }

  private void validateUpdatableItem(SysDictItem item) {
    if (!isSuperAdmin() && !currentTenantIdForWrite().equals(item.getTenantId())) {
      throw new ServiceException("无权修改当前字典项");
    }
  }

  private void validateItemIdentity(SysDictItem exist, SysDictItemUpdateQO obj) {
    if (!Objects.equals(exist.getDictId(), obj.getDictId())) {
      throw new ServiceException("字典项所属字典不允许修改");
    }
    if (Boolean.TRUE.equals(exist.getCodeReferenced()) && !Objects.equals(exist.getItemValue(), obj.getItemValue())) {
      throw new ServiceException("系统引用字典项值不允许修改");
    }
  }

  private void validateRemovableItem(SysDictItem item) {
    if (Boolean.TRUE.equals(item.getCodeReferenced())) {
      throw new ServiceException("系统引用字典项不允许删除");
    }
    if (!isSuperAdmin() && !currentTenantIdForWrite().equals(item.getTenantId())) {
      throw new ServiceException("无权删除当前字典项");
    }
  }

  private void validateDuplicate(Long dictId, String itemValue, String label, Long excludeId) {
    long count = sysDictItemMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
      .eq(SysDictItem::getDictId, dictId)
      .and(c -> c
        .eq(SysDictItem::getItemValue, itemValue)
        .or()
        .eq(SysDictItem::getLabel, label))
      .ne(excludeId != null, SysDictItem::getId, excludeId));
    if (count > 0) {
      throw new ServiceException("字典项值或名称不能重复");
    }
  }

  private void resolveEffectiveDictQuery(SysDictItemListQO query) {
    if (query.getDictId() != null) {
      return;
    }
    if (query.getDictKey() == null || query.getDictKey().trim().isEmpty()) {
      return;
    }
    SysDict effectiveDict = sysDictService.resolveEffectiveDict(query.getDictKey(), query.getTenantId());
    query.setDictId(effectiveDict.getId());
  }

  private void resolveEffectiveDictQuery(SysDictItemPageQO query) {
    if (query.getDictId() != null) {
      return;
    }
    if (query.getDictKey() == null || query.getDictKey().trim().isEmpty()) {
      return;
    }
    SysDict effectiveDict = sysDictService.resolveEffectiveDict(query.getDictKey(), query.getTenantId());
    query.setDictId(effectiveDict.getId());
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
