package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 字典保存入参
 */
@Schema(title = "字典保存入参")
@Data
public class SysDictSaveQO {

  @Schema(title = "字典名称")
  @NotBlank(message = "字典名称不能为空")
  private String name;
  @Schema(title = "字典标识")
  @NotBlank(message = "字典标识不能为空")
  private String key;
  @Schema(title = "备注")
  private String remarks;
}
