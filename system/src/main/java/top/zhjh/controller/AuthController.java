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
import top.zhjh.model.vo.CaptchaVO;
import top.zhjh.model.vo.SysUserDetailVO;
import top.zhjh.service.LoginSecurityService;
import top.zhjh.service.SysUserService;

import javax.annotation.Resource;

/**
 * 权限控制器
 */
@RequestMapping
@RestController
public class AuthController extends BaseController {

  @Resource
  // 登录成功后返回当前用户详情。
  private SysUserService sysUserService;
  @Resource
  // 负责验证码、失败锁定与密码强度等登录安全能力。
  private LoginSecurityService loginSecurityService;

  @SaIgnore
  @Operation(summary = "验证码")
  @GetMapping("/captcha")
  public R<CaptchaVO> captcha() {
    // 登录前先下发验证码标识和图片内容，前端据此完成人机校验。
    return ok(loginSecurityService.createCaptcha());
  }

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
    // 登录参数走表单提交，包含租户、验证码与用户名密码。
    return ok(sysUserService.login(query));
  }

  @Operation(summary = "登出")
  @PostMapping("/logout")
  public R<?> logout() {
    StpUtil.logout();
    return ok();
  }
}
