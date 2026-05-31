package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageQO;
import top.zhjh.model.entity.SysDictItem;

/**
 * 字典项分页入参
 */
@Schema(title = "字典项分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictItemPageQO extends PageQO<SysDictItem> {

  @Schema(title = "字典ID")
  private Long dictId;
  @Schema(title = "字典标识")
  private String dictKey;
  @Schema(title = "字典项值")
  private String itemValue;
  @Schema(title = "字典项名称")
  private String label;
  @Schema(title = "描述")
  private String description;
  @Schema(hidden = true)
  private Long tenantId;
  @Schema(hidden = true)
  private Boolean superAdmin;
}
