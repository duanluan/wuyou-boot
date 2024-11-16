package top.zhjh.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.entity.SysUser;
import top.zhjh.model.qo.*;
import top.zhjh.service.SysUserService;
import top.zhjh.struct.SysUserStruct;

import javax.annotation.Resource;

/**
 * 用户 控制器
 */
@Tag(name = "用户")
@Slf4j
@RequestMapping(value = "/sys/users", consumes = MediaType.ALL_VALUE)
@RestController
public class SysUserController extends BaseController {

  @Resource
  private SysUserService sysUserService;

  @Operation(summary = "注册")
  @PostMapping("/register")
  public R register(@Validated SysUserRegisterQO query) {
    sysUserService.register(query.getNickname(), query.getUsername(), query.getPassword());
    return ok();
  }

  @SaIgnore
  @Operation(summary = "登录")
  @PostMapping("/login")
  public R login(@Validated SysUserLoginQO query) {
    SysUser loggedInUser = sysUserService.login(query.getUsername(), query.getPassword());
    return ok(SysUserStruct.INSTANCE.toDetailVO(loggedInUser));
  }

  @Operation(summary = "登出")
  @PostMapping("/logout")
  public R logout() {
    StpUtil.logout();
    return ok();
  }

  @Operation(summary = "用户列表")
  @GetMapping
  public R<?> list(@Validated SysUserPageQO query) {
    if (query.getCurrent() == 0) {
      return ok(sysUserService.list(query));
    }
    return ok(sysUserService.page(query));
  }

  // @Operation(summary = "用户详情")
  // @GetMapping("/{id}")
  // public R<SysUserDetailVO> get(@Validated SysUserGetQO query) {
  //   return ok(sysUserService.getById(query.getId()));
  // }

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

  @Operation(summary = "删除用户")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R remove(@Validated SysUserRemoveQO query) {
    return removeR(sysUserService.remove(query));
  }
}
