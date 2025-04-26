package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.MenuType;

import java.util.List;

/**
 * 菜单树入参
 */
@Data
public class SysMenuTreeTableQO {
  @Schema(title = "是否不构建树")
  private Boolean notBuildTree;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;
  @Schema(title = "类型列表：1: 目录, 2: 菜单, 3: 按钮，逗号分隔")
  private List<MenuType> types;
  @Schema(title = "角色编码列表")
  private List<String> roleCodes;

  @Schema(title = "列表中角色编码拥有的菜单要选中")
  private List<String> checkedRoleCodes;
  /**
   * 选中 SQL 是否只查询 ID，仅 XML 内部使用
   */
  private Boolean checkedOnlyId;
  @Schema(title = "是否获取全部和选中，false只获取选中")
  private Boolean isAllAndChecked;
}
