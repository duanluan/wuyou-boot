package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class SysDeptUpdateQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "部门ID错误")
  private Long id;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "父级 ID")
  @Min(value = 1, message = "父级ID错误")
  private Long parentId;
  @Schema(title = "排序")
  @Min(value = 1, message = "排序错误")
  private Long sort;
}
