package top.zhjh.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.enums.CommonStatus;

/**
 * 部门
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDept extends BaseEntity {
  /**
   * 名称
   */
  @Schema(title = "名称")
  private String name;
  /**
   * 父级 ID
   */
  @Schema(title = "父级 ID")
  private Long parentId;
  /**
   * 排序
   */
  @Schema(title = "排序")
  private Integer sort;
  /**
   * 状态：0: 禁用, 1: 启用
   */
  @Schema(title = "状态")
  private CommonStatus status;
}
