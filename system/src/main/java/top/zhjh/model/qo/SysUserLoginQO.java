package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 用户登录入参
 */
@Schema(title = "用户登录入参")
@Data
public class SysUserLoginQO {

  @Schema(title = "用户名")
  @NotBlank(message = "用户名不能为空")
  private String username;
  @Schema(title = "密码")
  @NotBlank(message = "密码不能为空")
  private String password;
  @Schema(title = "租户ID")
  @Min(value = 1, message = "租户ID错误")
  private Long tenantId;
}
