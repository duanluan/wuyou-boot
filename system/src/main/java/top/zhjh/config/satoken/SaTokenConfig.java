package top.zhjh.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.csaf.lang.StrUtil;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.service.SysMenuService;

import java.util.List;

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
      // 从数据库查询有路径的菜单
      List<SysMenu> menuList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().isNotNull(SysMenu::getPath));
      for (SysMenu menu : menuList) {
        String method = menu.getMethod();
        String path = menu.getPath();
        String permission = menu.getPermission();

        if (StrUtil.isNotBlank(permission)) {
          // 校验权限
          SaRouter.match(SaHttpMethod.toEnum(method)).match(path).check(r -> StpUtil.checkPermission(menu.getPermission()));
        } else if (Boolean.TRUE.equals(menu.getNeedToLogin())) {
          // 登录校验
          SaRouter.match(SaHttpMethod.toEnum(method)).match(path, r -> StpUtil.checkLogin());
        }
      }
    })).addPathPatterns("/**");
  }
}
