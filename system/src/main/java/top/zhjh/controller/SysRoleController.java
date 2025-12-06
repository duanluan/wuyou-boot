package top.zhjh.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.qo.*;
import top.zhjh.service.SysRoleService;
import top.zhjh.struct.SysRoleStruct;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

/**
 * 角色 控制器
 */
@Tag(name = "角色")
@Slf4j
@RequestMapping("/sys/roles")
@RestController
public class SysRoleController extends BaseController {

  @Resource
  private SysRoleService sysRoleService;

  @Operation(summary = "角色列表")
  @GetMapping
  public R<?> list(@Validated SysRolePageQO query) {
    TenantContext.disable();
    // 添加登录用户（没有租户的）角色
    query.setIsAddLoginUserRole(true);
    query.setLoginUserId(StpUtil.getLoginIdAsLong());
    query.setTenantId(StpExtUtil.getTenantId());

    if (query.getCurrent() == 0) {
      return ok(sysRoleService.list(SysRoleStruct.INSTANCE.to(query)));
    }
    return ok(sysRoleService.page(query));
  }

  @Operation(summary = "角色详情")
  @GetMapping("/{id}")
  public R<SysRole> get(@Schema(title = "角色ID") @Min(value = 1, message = "ID错误") @PathVariable Long id) {
    return ok(sysRoleService.getById(id));
  }

  @Operation(summary = "保存角色")
  @PostMapping
  public R<?> save(@RequestBody @Validated SysRoleSaveQO obj) {
    return saveR(sysRoleService.save(obj));
  }

  @Operation(summary = "更新角色")
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated SysRoleUpdateQO obj) {
    return updateR(sysRoleService.update(obj));
  }

  @Operation(summary = "更新角色状态")
  @PatchMapping("/{id}/status")
  public R<?> updateStatus(@RequestBody @Validated SysRoleUpdateStatusQO obj) {
    return updateR(sysRoleService.updateStatus(obj));
  }

  @Operation(summary = "更新角色菜单权限")
  @PatchMapping("/{id}/menus")
  public R<?> updateMenus(@RequestBody @Validated SysRoleUpdateMenusQO obj) {
    return updateR(sysRoleService.updateMenus(obj));
  }

  @Operation(summary = "更新角色数据权限")
  @PatchMapping("/{id}/dataScope")
  public R<?> updateDataScope(@RequestBody @Validated SysRoleUpdateDataScopeQO obj) {
    return updateR(sysRoleService.updateDataScope(obj));
  }

  @Operation(summary = "删除角色")
  @DeleteMapping("/{ids}")
  public R<?> remove(@Validated SysRoleRemoveQO query) {
    return removeR(sysRoleService.remove(query));
  }
}
