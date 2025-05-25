package top.zhjh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.qo.*;
import top.zhjh.service.SysUserService;
import top.zhjh.struct.SysUserStruct;

import javax.annotation.Resource;

/**
 * 用户 控制器
 */
@Tag(name = "用户")
@Slf4j
@RequestMapping(value = "/sys/users")
@RestController
public class SysUserController extends BaseController {

  @Resource
  private SysUserService sysUserService;

  @Operation(summary = "用户列表")
  @GetMapping
  public R<?> list(@Validated SysUserPageQO query) {
    if (query.getCurrent() == 0) {
      return ok(sysUserService.list(SysUserStruct.INSTANCE.to(query)));
    }
    return ok(sysUserService.page(query));
  }

  @Operation(summary = "保存用户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R save(@RequestBody @Validated SysUserSaveQO obj) {
    return saveR(sysUserService.save(obj));
  }

  @Operation(summary = "更新用户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R update(@RequestBody @Validated SysUserUpdateQO obj) {
    return updateR(sysUserService.update(obj));
  }

  @Operation(summary = "更新用户基本信息")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}/profile")
  public R updateProfile(@RequestBody @Validated(SysUserUpdateQO.UpdateProfileGroup.class) SysUserUpdateQO obj) {
    return updateR(sysUserService.updateById(SysUserStruct.INSTANCE.to(obj)));
  }

  @Operation(summary = "修改密码")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}/pwd")
  public R updatePwd(@RequestBody @Validated SysUserUpdatePwdQO obj) {
    return updateR(sysUserService.updatePwd(obj));
  }

  @Operation(summary = "删除用户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R remove(@Validated SysUserRemoveQO query) {
    return removeR(sysUserService.remove(query));
  }
}
