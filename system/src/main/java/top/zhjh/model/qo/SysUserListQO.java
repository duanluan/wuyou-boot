package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户分页入参
 */
@Schema(title = "SysUser 分页入参")
@Data
public class SysUserListQO {

  @Schema(title = "昵称")
  private String nickName;
  @Schema(title = "用户名")
  private String username;
  @Schema(title = "租户ID")
  private Long tenantId;
}
