package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 角色保存入参
 */
@Schema(title = "角色 保存入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleSaveQO  {

  @Schema(title = "编码")
  @NotBlank(message = "编码不能为空")
  private String code;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "描述")
  private String description;
}
