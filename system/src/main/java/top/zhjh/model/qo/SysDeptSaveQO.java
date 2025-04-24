package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 部门保存入参
 */
@Schema(title = "部门保存入参")
@Data
public class SysDeptSaveQO {

  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "父级 ID")
  @Min(value = 0, message = "父级ID错误")
  private Long parentId;
  @Schema(title = "顺序")
  @Min(value = 1, message = "顺序错误")
  private Long sort;
}
