package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户删除入参
 */
@Schema(title = "SysUser 删除入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserRemoveQO  {

  @Schema(title = "多个用户 ID，逗号分隔")
  private String ids;
  @Schema(title = "创建时间")
  @NotNull(message = "创建时间不能为空")
  private LocalDateTime createdTime;
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
}
