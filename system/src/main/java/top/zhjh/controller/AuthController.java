package top.zhjh.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.qo.SysUserLoginQO;
import top.zhjh.model.vo.SysUserDetailVO;
import top.zhjh.service.SysUserService;

import javax.annotation.Resource;

/**
 * 权限控制器
 */
@RequestMapping
@RestController
public class AuthController extends BaseController {

  @Resource
  private SysUserService sysUserService;

  @SaIgnore
  @Operation(summary = "当前登录用户信息")
  @GetMapping("/profile")
  public R<SysUserDetailVO> profile() {
    return ok(sysUserService.getDetail(StpUtil.getLoginIdAsLong()));
  }

  @SaIgnore
  @Operation(summary = "登录")
  @PostMapping("/login")
  public R<?> login(@Validated SysUserLoginQO query) {
    return ok(sysUserService.login(query.getUsername(), query.getPassword(), query.getTenantId()));
  }

  @Operation(summary = "登出")
  @PostMapping("/logout")
  public R<?> logout() {
    StpUtil.logout();
    return ok();
  }
}
