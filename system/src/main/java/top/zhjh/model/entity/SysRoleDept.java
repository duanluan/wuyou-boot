package top.zhjh.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.zhjh.enums.DataScopeActionType;

/**
 * 角色-部门（数据权限自定义）
 */
@AllArgsConstructor
@Data
public class SysRoleDept {
  /**
   * 角色 ID
   */
  private Long roleId;
  /**
   * 部门 ID
   */
  private Long deptId;
  /**
   * 类型：1: 查询, 2: 增删改
   */
  private DataScopeActionType dataScopeActionType;
}
