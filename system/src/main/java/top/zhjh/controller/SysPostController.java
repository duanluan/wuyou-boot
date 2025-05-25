package top.zhjh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.qo.SysPostPageQO;
import top.zhjh.model.qo.SysPostRemoveQO;
import top.zhjh.model.qo.SysPostSaveQO;
import top.zhjh.model.qo.SysPostUpdateQO;
import top.zhjh.service.SysPostService;
import top.zhjh.struct.SysPostStruct;

import javax.annotation.Resource;

/**
 * 岗位 控制器
 */
@Tag(name = "岗位")
@Slf4j
@RequestMapping(value = "/sys/posts")
@RestController
public class SysPostController extends BaseController {

  @Resource
  private SysPostService sysPostService;

  @Operation(summary = "岗位列表")
  @GetMapping
  public R<?> list(@Validated SysPostPageQO query) {
    if (query.getCurrent() == 0) {
      return ok(sysPostService.list(SysPostStruct.INSTANCE.to(query)));
    }
    return ok(sysPostService.page(query));
  }

  @Operation(summary = "保存岗位")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R<?> save(@RequestBody @Validated SysPostSaveQO obj) {
    return saveR(sysPostService.save(obj));
  }

  @Operation(summary = "更新岗位")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated(SysPostUpdateQO.UpdateGroup.class) SysPostUpdateQO obj) {
    return updateR(sysPostService.update(obj));
  }

  @Operation(summary = "更新岗位状态")
  @PatchMapping("/{id}/status")
  public R<?> updateStatus(@RequestBody @Validated(SysPostUpdateQO.UpdateStatusGroup.class) SysPostUpdateQO obj) {
    return updateR(sysPostService.updateStatus(obj));
  }

  @Operation(summary = "删除岗位")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R<?> remove(@Validated SysPostRemoveQO query) {
    return removeR(sysPostService.remove(query));
  }
}
