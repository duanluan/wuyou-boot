package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 租户保存入参
 */
@Schema(title = "租户保存入参")
@Data
public class SysTenantSaveQO {

  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "排序")
  @Min(value = 1, message = "排序错误")
  private Long sort;
}
