package top.zhjh.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.qo.SysDictItemPageQO;
import top.zhjh.model.qo.SysDictItemRemoveQO;
import top.zhjh.model.qo.SysDictItemSaveQO;
import top.zhjh.model.qo.SysDictItemUpdateQO;
import top.zhjh.service.SysDictItemService;
import top.zhjh.struct.SysDictItemStruct;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

/**
 * 字典项 控制器
 */
@Tag(name = "字典项")
@Slf4j
@RequestMapping("/sys/dict-items")
@RestController
public class SysDictItemController extends BaseController {

  @Resource
  private SysDictItemService sysDictItemService;

  @Operation(summary = "字典项列表")
  @GetMapping
  public R<?> list(@Validated SysDictItemPageQO query) {
    boolean publicDictRead = query.getCurrent() == 0
      && (query.getDictId() != null || (query.getDictKey() != null && !query.getDictKey().trim().isEmpty()));
    if (!publicDictRead) {
      StpUtil.checkPermission("sys:dict");
    }
    if (query.getCurrent() == 0) {
      return ok(sysDictItemService.list(SysDictItemStruct.INSTANCE.to(query)));
    }
    return ok(sysDictItemService.page(query));
  }

  @Operation(summary = "字典项详情")
  @SaCheckPermission("sys:dict")
  @GetMapping("/{id}")
  public R<SysDictItem> get(@Min(value = 1, message = "ID错误") @PathVariable Long id) {
    return ok(sysDictItemService.get(id));
  }

  @Operation(summary = "保存字典项")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @SaCheckPermission("sys:dict")
  @PostMapping
  public R<?> save(@RequestBody @Validated SysDictItemSaveQO obj) {
    return saveR(sysDictItemService.save(obj));
  }

  @Operation(summary = "更新字典项")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @SaCheckPermission("sys:dict")
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated(SysDictItemUpdateQO.UpdateGroup.class) SysDictItemUpdateQO obj) {
    return updateR(sysDictItemService.update(obj));
  }

  @Operation(summary = "删除字典项")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @SaCheckPermission("sys:dict")
  @DeleteMapping("/{ids}")
  public R<?> remove(@Validated SysDictItemRemoveQO query) {
    return removeR(sysDictItemService.remove(query));
  }
}
