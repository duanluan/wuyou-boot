package top.zhjh.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.enums.CommonStatus;

/**
 * 租户
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenant extends BaseEntity {
  /**
   * 名称
   */
  @Schema(title = "名称")
  private String name;
  /**
   * 顺序
   */
  @Schema(title = "顺序")
  private Integer sort;
  /**
   * 状态：0: 禁用, 1: 启用
   */
  @Schema(title = "状态")
  private CommonStatus status;
}
