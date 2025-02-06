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
import top.zhjh.model.qo.SysTenantPageQO;
import top.zhjh.model.qo.SysTenantRemoveQO;
import top.zhjh.model.qo.SysTenantSaveQO;
import top.zhjh.model.qo.SysTenantUpdateQO;
import top.zhjh.service.SysTenantService;
import top.zhjh.struct.SysTenantStruct;

import javax.annotation.Resource;

/**
 * 租户 控制器
 */
@Tag(name = "租户")
@Slf4j
@RequestMapping(value = "/sys/tenants", consumes = MediaType.ALL_VALUE)
@RestController
public class SysTenantController extends BaseController {

  @Resource
  private SysTenantService sysTenantService;

  @Operation(summary = "租户列表")
  @GetMapping
  public R<?> list(@Validated SysTenantPageQO query) {
    if (query.getCurrent() == 0) {
      return ok(sysTenantService.list(SysTenantStruct.INSTANCE.to(query)));
    }
    return ok(sysTenantService.page(query));
  }

  @Operation(summary = "保存租户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R<?> save(@RequestBody @Validated SysTenantSaveQO obj) {
    return saveR(sysTenantService.save(obj));
  }

  @Operation(summary = "更新租户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated(SysTenantUpdateQO.UpdateGroup.class) SysTenantUpdateQO obj) {
    return updateR(sysTenantService.update(obj));
  }

  @Operation(summary = "更新租户状态")
  @PatchMapping("/{id}/status")
  public R<?> updateStatus(@RequestBody @Validated(SysTenantUpdateQO.UpdateStatusGroup.class) SysTenantUpdateQO obj) {
    return updateR(sysTenantService.updateStatus(obj));
  }

  @Operation(summary = "删除租户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R<?> remove(@Validated SysTenantRemoveQO query) {
    return removeR(sysTenantService.remove(query));
  }
}
