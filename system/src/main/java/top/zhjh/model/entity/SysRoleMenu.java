package top.zhjh.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 角色菜单关联
 */
@AllArgsConstructor
@Data
public class SysRoleMenu {
  /**
   * 角色 ID
   */
  private Long roleId;
  /**
   * 菜单 ID
   */
  private Long menuId;
}
