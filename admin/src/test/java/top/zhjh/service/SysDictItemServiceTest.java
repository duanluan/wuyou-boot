package top.zhjh.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDictItemMapper;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.qo.SysDictItemUpdateQO;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SysDictItemServiceTest {

  @Test
  void validateItemIdentityRejectsReferencedItemValueChange() {
    SysDictItem exist = item(1L, "0", true);
    SysDictItemUpdateQO update = update(1L, "1");

    ServiceException exception = assertThrows(ServiceException.class, () -> validateItemIdentity(exist, update));

    assertEquals("系统引用字典项值不允许修改", exception.getMessage());
  }

  @Test
  void validateItemIdentityRejectsAnyDictIdChange() {
    SysDictItem exist = item(1L, "custom_a", false);
    SysDictItemUpdateQO update = update(2L, "custom_a");

    ServiceException exception = assertThrows(ServiceException.class, () -> validateItemIdentity(exist, update));

    assertEquals("字典项所属字典不允许修改", exception.getMessage());
  }

  @Test
  void validateItemIdentityAllowsNormalItemValueChangeInsideSameDict() {
    SysDictItem exist = item(1L, "custom_a", false);
    SysDictItemUpdateQO update = update(1L, "custom_b");

    assertDoesNotThrow(() -> validateItemIdentity(exist, update));
  }

  @Test
  void validateDuplicateRejectsSameValueOrLabel() {
    SysDictItemService service = serviceWithDuplicateCount(1L);

    ServiceException exception = assertThrows(ServiceException.class,
      () -> validateDuplicate(service, 1L, "custom_b", "自定义B", 10L));

    assertEquals("字典项值或名称不能重复", exception.getMessage());
  }

  @Test
  void validateDuplicateAllowsNoConflict() {
    SysDictItemService service = serviceWithDuplicateCount(0L);

    assertDoesNotThrow(() -> validateDuplicate(service, 1L, "custom_b", "自定义B", 10L));
  }

  private void validateItemIdentity(SysDictItem exist, SysDictItemUpdateQO update) {
    ReflectionTestUtils.invokeMethod(new SysDictItemService(), "validateItemIdentity", exist, update);
  }

  private void validateDuplicate(SysDictItemService service, Long dictId, String itemValue, String label, Long excludeId) {
    ReflectionTestUtils.invokeMethod(service, "validateDuplicate", dictId, itemValue, label, excludeId);
  }

  private SysDictItemService serviceWithDuplicateCount(Long count) {
    SysDictItemService service = new SysDictItemService();
    SysDictItemMapper mapper = mock(SysDictItemMapper.class);
    when(mapper.selectCount(any(Wrapper.class))).thenReturn(count);
    ReflectionTestUtils.setField(service, "sysDictItemMapper", mapper);
    return service;
  }

  private SysDictItem item(Long dictId, String itemValue, boolean codeReferenced) {
    SysDictItem item = new SysDictItem();
    item.setDictId(dictId);
    item.setItemValue(itemValue);
    item.setCodeReferenced(codeReferenced);
    return item;
  }

  private SysDictItemUpdateQO update(Long dictId, String itemValue) {
    SysDictItemUpdateQO update = new SysDictItemUpdateQO();
    update.setDictId(dictId);
    update.setItemValue(itemValue);
    return update;
  }
}
