package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(title = "角色列表入参")
@Data
public class SysRoleListQO {

  @Schema(title = "是否添加登录用户角色", hidden = true)
  private Boolean isAddLoginUserRole;
  @Schema(title = "登录用户ID", hidden = true)
  private Long loginUserId;
  @Schema(title = "租户ID", hidden = true)
  private Long tenantId;

  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
}
