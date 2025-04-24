package top.zhjh.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 租户-用户
 */
@AllArgsConstructor
@Data
public class SysTenantUser {
  /**
   * 租户 ID
   */
  private Long tenantId;
  /**
   * 用户 ID
   */
  private Long userId;
}
