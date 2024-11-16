package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.MyPage;
import top.zhjh.model.entity.SysRole;

/**
 * 角色分页入参
 */
@Schema(title = "角色 分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageQO extends MyPage<SysRole> {

  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
}
