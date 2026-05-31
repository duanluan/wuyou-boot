package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.DictSourceScope;

/**
 * 字典列表入参
 */
@Schema(title = "字典列表入参")
@Data
public class SysDictListQO {

  @Schema(title = "字典类型")
  private String keyword;
  @Schema(title = "是否只查询当前生效字典")
  private Boolean effectiveOnly;
  @Schema(title = "来源范围")
  private DictSourceScope sourceScope;
  @Schema(hidden = true)
  private Long tenantId;
  @Schema(hidden = true)
  private Boolean superAdmin;
}
