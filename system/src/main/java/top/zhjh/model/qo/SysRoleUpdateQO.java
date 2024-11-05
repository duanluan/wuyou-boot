package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

/**
 * 角色更新入参
 */
@Schema(title = "角色 更新入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleUpdateQO extends SysRoleSaveQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色ID错误")
  private Long id;
}
