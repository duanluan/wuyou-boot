package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册入参
 */
@Schema(title = "用户注册入参")
@Data
public class SysUserRegisterQO {

  @Schema(title = "昵称")
  @NotBlank(message = "昵称不能为空")
  private String nickname;
  @Schema(title = "用户名")
  @NotBlank(message = "用户名不能为空")
  private String username;
  @Schema(title = "密码")
  @NotBlank(message = "密码不能为空")
  private String password;
}
