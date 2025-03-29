package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.enums.DataScopeType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色更新数据权限入参
 */
@Schema(title = "角色更新数据权限入参")
@Data
public class SysRoleUpdateDataScopeQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色ID错误")
  private Long id;
  @Schema(title = "查询数据权限")
  @NotNull(message = "查询数据权限不能为空")
  private DataScopeType queryDataScope;
  @Schema(title = "增删改数据权限")
  @NotNull(message = "增删改数据权限不能为空")
  private DataScopeType updateDataScope;
  @Schema(title = "查询数据权限自定义部门ID列表")
  private List<Long> queryDataScopeDeptIds;
  @Schema(title = "增删改数据权限自定义部门ID列表")
  private List<Long> updateDataScopeDeptIds;
}
