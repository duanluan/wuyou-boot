package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.DataScopeType;

import java.util.List;

@Schema(title = "角色数据权限详情")
@Data
public class SysRoleDataScopeVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "编码")
  private String code;
  @Schema(title = "查询数据权限")
  private DataScopeType queryDataScope;
  @Schema(title = "增删改数据权限")
  private DataScopeType updateDataScope;
  @Schema(title = "查询数据权限部门ID集合")
  private List<Long> queryDataScopeDeptIds;
  @Schema(title = "增删改数据权限部门ID集合")
  private List<Long> updateDataScopeDeptIds;
}