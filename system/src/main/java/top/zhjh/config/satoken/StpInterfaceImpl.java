package top.zhjh.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;
import top.zhjh.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component
public class StpInterfaceImpl implements StpInterface {

  @Resource
  private SysUserService sysUserService;

  /**
   * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
   */
  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    return sysUserService.listRoleCodes((String) loginId);
  }

  /**
   * 返回一个账号所拥有的权限码集合
   */
  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return sysUserService.listPermission((String) loginId);
  }
}
