package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 用户保存入参
 */
@Schema(title = "SysUser 保存入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserSaveQO  {

  @Schema(title = "用户名")
  @NotBlank(message = "用户名不能为空")
  private String username;
  @Schema(title = "密码")
  @NotBlank(message = "密码不能为空")
  private String password;
  @Schema(title = "密码盐")
  @NotBlank(message = "密码盐不能为空")
  private String passwordSalt;
  @Schema(title = "昵称")
  @NotBlank(message = "昵称不能为空")
  private String nickName;

  /**
   * 用于保存后返回主键
   */
  private Long id;
}
