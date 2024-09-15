package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageVO;

/**
 * 用户分页响应
 */
@Schema(title = "用户分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserPageVO extends PageVO {

  @Schema(title = "用户名")
  private String username;
  @Schema(title = "密码")
  private String password;
  @Schema(title = "密码盐")
  private String passwordSalt;
  @Schema(title = "昵称")
  private String nickName;
}
