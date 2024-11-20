package top.zhjh.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.service.SysMenuService;
import top.zhjh.service.SysRoleUserService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StartInit {

  @Resource
  private SysMenuService sysMenuService;
  @Resource
  private SysRoleUserService sysRoleUserService;

  @PostConstruct
  public void initSuperAdminData() {
    // 获取所有菜单 ID
    List<SysMenu> menuList = sysMenuService.lambdaQuery().select(SysMenu::getId).list();
    if (menuList.isEmpty()) {
      log.warn("菜单为空");
      return;
    }
    List<Long> menuIds = menuList.stream().map(SysMenu::getId).collect(Collectors.toList());
// TODO StpExtendUtil 中封装 superAdminRole，加缓存



  }
}
