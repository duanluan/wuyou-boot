package top.zhjh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {
  /**
   * 超级管理员
   */
  SUPER_ADMIN("superAdmin"),
  ;

  private final String code;
}
