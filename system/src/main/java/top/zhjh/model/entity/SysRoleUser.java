package top.zhjh.model.entity;

import lombok.Data;

/**
 * 角色用户关联表
 */
@Data
public class SysRoleUser {
  /**
   * 角色 ID
   */
  private Long roleId;
  /**
   * 用户 ID
   */
  private Long userId;
}
