package top.zhjh.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 部门-用户
 */
@AllArgsConstructor
@Data
public class SysDeptUser {
  /**
   * 部门 ID
   */
  private Long deptId;
  /**
   * 用户 ID
   */
  private Long userId;
}
