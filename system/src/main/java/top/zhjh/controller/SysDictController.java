package top.zhjh.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.entity.SysDict;
import top.zhjh.model.qo.SysDictPageQO;
import top.zhjh.model.qo.SysDictRemoveQO;
import top.zhjh.model.qo.SysDictSaveQO;
import top.zhjh.model.qo.SysDictUpdateQO;
import top.zhjh.service.SysDictService;
import top.zhjh.struct.SysDictStruct;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

/**
 * 字典 控制器
 */
@Tag(name = "字典")
@Slf4j
@SaCheckPermission("sys:dict")
@RequestMapping("/sys/dicts")
@RestController
public class SysDictController extends BaseController {

  @Resource
  private SysDictService sysDictService;

  @Operation(summary = "字典列表")
  @GetMapping
  public R<?> list(@Validated SysDictPageQO query) {
    if (query.getCurrent() == 0) {
      return ok(sysDictService.list(SysDictStruct.INSTANCE.to(query)));
    }
    return ok(sysDictService.page(query));
  }

  @Operation(summary = "字典详情")
  @GetMapping("/{id}")
  public R<SysDict> get(@Min(value = 1, message = "ID错误") @PathVariable Long id) {
    return ok(sysDictService.get(id));
  }

  @Operation(summary = "保存字典")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R<?> save(@RequestBody @Validated SysDictSaveQO obj) {
    return saveR(sysDictService.save(obj));
  }

  @Operation(summary = "更新字典")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated(SysDictUpdateQO.UpdateGroup.class) SysDictUpdateQO obj) {
    return updateR(sysDictService.update(obj));
  }

  @Operation(summary = "删除字典")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R<?> remove(@Validated SysDictRemoveQO query) {
    return removeR(sysDictService.remove(query));
  }
}
