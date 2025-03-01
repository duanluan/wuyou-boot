package top.zhjh.util;

import cn.dev33.satoken.stp.StpUtil;
import top.csaf.bean.BeanUtil;
import top.csaf.lang.NumberUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.enums.RoleEnum;
import top.zhjh.model.entity.SysRole;

/**
 * Sa-Token StpUtil 扩展
 */
public class StpExtUtil {

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

  /**
   * 获取当前登录用户的租户 ID
   *
   * @return 当前登录用户的租户 ID
   */
  public static Long getTenantId() {
    return NumberUtil.createLong(StpUtil.getExtra(BeanUtil.getPropertyName(BaseEntity::getTenantId)));
  }
}
