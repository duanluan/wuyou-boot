package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 字典更新入参
 */
@Schema(title = "字典更新入参")
@Data
public class SysDictUpdateQO {

  public interface UpdateGroup {
  }

  @Schema(title = "ID")
  @Min(value = 1, message = "字典ID错误", groups = {UpdateGroup.class})
  private Long id;
  @Schema(title = "字典名称")
  @NotBlank(message = "字典名称不能为空", groups = {UpdateGroup.class})
  private String name;
  @Schema(title = "备注")
  private String remarks;
}
