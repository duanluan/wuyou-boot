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
  // 登录用户名，和数据库中的 sys_user.username 对应。
  private String username;
  @Schema(title = "密码")
  @NotBlank(message = "密码不能为空")
  // 登录原始密码，服务层会使用 PasswordCipher 做比对。
  private String password;
  @Schema(title = "租户ID")
  @Min(value = 1, message = "租户ID错误")
  // 多租户登录时的租户标识；超管可为空或跨租户使用。
  private Long tenantId;
  @Schema(title = "验证码标识")
  // 验证码图片生成后返回给前端的唯一标识，用于到 Redis 中取验证码答案。
  private String captchaId;
  @Schema(title = "验证码")
  // 用户在登录页输入的验证码内容。
  private String captchaCode;
}
