package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.DataScopeActionType;

import java.util.List;

/**
 * 部门树入参
 */
@Data
public class SysDeptTreeQO {
  @Schema(title = "是否不构建树")
  private Boolean notBuildTree;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;

  @Schema(title = "角色编码列表")
  private List<String> roleCodes;
  @Schema(title = "数据权限操作类型")
  private DataScopeActionType dataScopeActionType;
  @Schema(title = "是否获取全部和选中")
  private Boolean isAllAndChecked;
}
