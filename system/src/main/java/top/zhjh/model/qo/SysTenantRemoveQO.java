package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 租户删除入参
 */
@Schema(title = "租户删除入参")
@Data
public class SysTenantRemoveQO {

  @Schema(title = "租户ID列表")
  @Size(min = 1, message = "租户ID列表不能为空")
  private List<Long> ids;
}
