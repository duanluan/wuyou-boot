package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageQO;
import top.zhjh.model.entity.SysRole;

@Schema(title = "角色分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageQO extends PageQO<SysRole> {

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
