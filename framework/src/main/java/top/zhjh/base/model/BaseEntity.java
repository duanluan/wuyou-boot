package top.zhjh.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 基础实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity extends BaseEntityNoTenant implements Serializable {

  @Schema(title = "租户 ID")
  private Long tenantId;
}
