package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageQO;
import top.zhjh.enums.CommonStatus;
import top.zhjh.model.entity.SysDept;

/**
 * 租户分页入参
 */
@Schema(title = "租户分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantPageQO extends PageQO<SysDept> {

  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;
}
