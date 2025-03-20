package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.csaf.coll.CollUtil;
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
  @Schema(title = "是否获取全部和选中")
  private Boolean isAllAndChecked;
}
