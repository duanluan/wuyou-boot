package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.enums.CommonStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 角色更新入参
 */
@Schema(title = "角色更新入参")
@Data
public class SysRoleUpdateQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色ID错误")
  private Long id;
  @Schema(title = "编码")
  @NotBlank(message = "编码不能为空")
  private String code;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "描述")
  private String description;
  @Schema(title = "顺序")
  @Min(value = 1, message = "顺序不能为空")
  private Long sort;
  @Schema(title = "状态")
  @NotNull(message = "状态不能为空")
  private CommonStatus status;
}
