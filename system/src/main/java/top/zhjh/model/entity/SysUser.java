package top.zhjh.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntityNoTenant;

/**
 * 系统用户
 */
@Schema(title = "系统用户")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends BaseEntityNoTenant {

  /**
   * 用户名
   */
  @Schema(title = "用户名")
  private String username;
  /**
   * 密码
   */
  @Schema(title = "密码")
  private String password;
  /**
   * 昵称
   */
  @Schema(title = "昵称")
  private String nickName;
}
