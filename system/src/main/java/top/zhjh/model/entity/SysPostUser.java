package top.zhjh.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 岗位-用户
 */
@AllArgsConstructor
@Data
public class SysPostUser {
  /**
   * 岗位 ID
   */
  private Long postId;
  /**
   * 用户 ID
   */
  private Long userId;
}
