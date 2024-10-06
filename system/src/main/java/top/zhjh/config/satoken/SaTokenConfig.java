package top.zhjh.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.service.SysMenuService;

/**
 * Sa-Token 配置
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

  @Autowired
  private SysMenuService sysMenuService;

  /**
   * 注册 Sa-Token 拦截器，打开注解式鉴权功能
   *
   * @param registry
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 注册路由拦截器，自定义认证规则
    registry.addInterceptor(new SaInterceptor(handler -> {
      // 所有路由登录校验
      SaRouter.match("/**", r -> StpUtil.checkLogin());

      // 循环有路径、权限，需登录的菜单
      for (SysMenu sysMenu : sysMenuService.lambdaQuery()
        .isNotNull(SysMenu::getPath)
        .isNotNull(SysMenu::getPermission)
        .eq(SysMenu::getNeedToLogin, true).list()) {
        // 校验权限
        SaRouter.match(sysMenu.getPath(), r -> StpUtil.checkPermission(sysMenu.getPermission()));
      }
    })).addPathPatterns("/**");
  }
}
