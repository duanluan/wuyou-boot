package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.csaf.lang.StrUtil;

@Data
public class SysMenuTreeQO {
  @Schema(title = "多个类型：1: 目录, 2: 菜单, 3: 按钮，逗号分隔")
  private String types;

  public boolean isNotBlankTypes() {
    return StrUtil.isNotBlank(types);
  }
}
