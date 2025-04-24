package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageQO;
import top.zhjh.model.entity.SysUser;

/**
 * 用户分页入参
 */
@Schema(title = "SysUser 分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserListQO extends PageQO<SysUser> {

  @Schema(title = "昵称")
  private String nickName;
  @Schema(title = "用户名")
  private String username;
  @Schema(title = "租户ID")
  private Long tenantId;
}
