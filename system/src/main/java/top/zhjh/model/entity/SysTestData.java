package top.zhjh.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntity;

/**
 * 数据权限测试实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysTestData extends BaseEntity {

  @Schema(title = "值")
  private String value;
  @Schema(title = "部门ID")
  private Long deptId;
}