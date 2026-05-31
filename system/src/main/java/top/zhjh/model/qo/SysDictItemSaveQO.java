package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 字典项保存入参
 */
@Schema(title = "字典项保存入参")
@Data
public class SysDictItemSaveQO {

  @Schema(title = "字典ID")
  @NotNull(message = "字典不能为空")
  @Min(value = 1, message = "字典ID错误")
  private Long dictId;
  @Schema(title = "字典项值")
  @NotBlank(message = "字典项值不能为空")
  private String itemValue;
  @Schema(title = "字典项名称")
  @NotBlank(message = "字典项名称不能为空")
  private String label;
  @Schema(title = "描述")
  private String description;
  @Schema(title = "排序")
  @NotNull(message = "排序不能为空")
  @Min(value = 1, message = "排序错误")
  private Long sortOrder;
  @Schema(title = "备注")
  private String remarks;
}
