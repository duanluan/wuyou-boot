package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;

/**
 * 租户列表入参
 */
@Schema(title = "租户列表入参")
@Data
public class SysTenantListQO {

  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;
}
