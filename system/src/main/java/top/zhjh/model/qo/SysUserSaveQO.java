package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户保存入参
 */
@Schema(title = "用户保存入参")
@Data
public class SysUserSaveQO  {

  @Schema(title = "用户名")
  @NotBlank(message = "用户名不能为空")
  private String username;
  @Schema(title = "昵称")
  @NotBlank(message = "昵称不能为空")
  private String nickName;
  @Schema(title = "密码")
  @NotBlank(message = "密码不能为空")
  private String password;
  @Schema(title = "角色ID列表")
  @Size(min = 1, message = "角色列表不能为空")
  private List<Long> roleIds;
  @Schema(title = "岗位列表")
  private List<Long> postIds;
}
