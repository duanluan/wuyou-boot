package top.zhjh.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 角色-用户
 */
@AllArgsConstructor
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
