package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户修改密码入参
 */
@Schema(title = "用户修改密码入参")
@Data
public class SysUserUpdatePwdQO {
  @Schema(title = "ID")
  @Min(value = 1, message = "ID错误")
  @NotNull(message = "ID不能为空")
  private Long id;
  @Schema(title = "旧密码")
  @NotNull(message = "旧密码不能为空")
  private String oldPassword;
  @Schema(title = "新密码")
  @NotNull(message = "新密码不能为空")
  private String newPassword;
  @Schema(title = "确认密码")
  @NotNull(message = "确认密码不能为空")
  private String confirmPassword;
}
