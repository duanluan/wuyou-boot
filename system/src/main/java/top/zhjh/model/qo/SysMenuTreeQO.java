package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.csaf.lang.StrUtil;

import java.util.List;

/**
 * 菜单树入参
 */
@Data
public class SysMenuTreeQO {
  @Schema(title = "多个类型：1: 目录, 2: 菜单, 3: 按钮，逗号分隔")
  private String types;
  @Schema(title = "角色编码列表")
  private List<String> roleCodeList;

  public boolean isNotBlankTypes() {
    return StrUtil.isNotBlank(types);
  }
}
