package top.zhjh.util;

import top.csaf.lang.StrUtil;
import top.zhjh.enums.RoleEnum;
import top.zhjh.model.entity.SysRole;

/**
 * Sa-Token StpUtil 扩展
 */
public class StpExtendUtil {

  /**
   * 是否为超级管理员
   *
   * @param sysRole 角色
   * @return 是否为超级管理员
   */
  public static boolean isSuperAdmin(SysRole sysRole) {
    if (sysRole == null || StrUtil.isBlank(sysRole.getCode())) {
      throw new IllegalArgumentException("sysRole is null or code is blank");
    }
    return RoleEnum.SUPER_ADMIN.getCode().equals(sysRole.getCode());
  }
}
