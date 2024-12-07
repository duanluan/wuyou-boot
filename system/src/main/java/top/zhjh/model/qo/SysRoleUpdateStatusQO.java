package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.enums.CommonStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 角色更新状态入参
 */
@Schema(title = "角色更新状态入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleUpdateStatusQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色ID错误")
  private Long id;
  @Schema(title = "状态")
  @NotNull(message = "状态不能为空")
  private CommonStatus status;
}
