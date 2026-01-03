package top.zhjh.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.service.SysMenuService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sa-Token 配置
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

  @Lazy
  @Resource
  private SysMenuService sysMenuService;

  /**
   * Sa-Token 整合 JWT（Simple 简单模式）
   *
   * @return StpLogicJwtForSimple
   */
  @Bean("stpLogicJwt")
  public StpLogic getStpLogicJwt() {
    return new StpLogicJwtForSimple();
  }

  /**
   * 注册 Sa-Token 拦截器，打开注解式鉴权功能
   *
   * @param registry 用于注册拦截器
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 注册路由拦截器，自定义认证规则
    registry.addInterceptor(new SaInterceptor(handler -> {
        // 所有路由登录校验
        SaRouter.match("/**", r -> StpUtil.checkLogin());

        TenantContext.disable();
        // 循环有路径、权限，需登录的菜单 TODO 缓存
        for (SysMenu sysMenu : sysMenuService.lambdaQuery()
          .isNotNull(SysMenu::getPath)
          .isNotNull(SysMenu::getPermission)
          .eq(SysMenu::getNeedToLogin, true).list()) {
          // 校验权限
          SaRouter.match(sysMenu.getPath(), r -> StpUtil.checkPermission(sysMenu.getPermission()));
        }
      })).addPathPatterns("/**")
      .excludePathPatterns("/sys/tenants");

    // 注册 TenantContext 清理拦截器，确保它在所有请求结束后执行清理，防止 ThreadLocal 内存泄漏
    registry.addInterceptor(new HandlerInterceptor() {
      @Override
      public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        TenantContext.clear();
      }
    }).addPathPatterns("/**");
  }
}
