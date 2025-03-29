package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色更新菜单权限入参
 */
@Schema(title = "角色更新菜单权限入参")
@Data
public class SysRoleUpdateMenusQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色ID错误")
  private Long id;
  @Schema(title = "菜单ID数组")
  @NotNull(message = "菜单ID数组不能为空")
  private List<Long> menuIds;
}
