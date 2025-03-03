package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.csaf.coll.CollUtil;

import java.util.List;

/**
 * 菜单树入参
 */
@Data
public class SysMenuTreeQO {
  @Schema(title = "是否刷新缓存")
  private Boolean isRefreshCache;
  @Schema(title = "类型列表：1: 目录, 2: 菜单, 3: 按钮，逗号分隔")
  private List<String> types;
  @Schema(title = "角色编码列表")
  private List<String> roleCodes;
  @Schema(title = "是否获取全部和选中")
  private Boolean isAllAndChecked;

  public boolean isNotBlankTypes() {
    return CollUtil.isNotEmpty(types);
  }
}
