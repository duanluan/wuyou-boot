package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 角色删除入参
 */
@Schema(title = "角色删除入参")
@Data
public class SysRoleRemoveQO {

  @Schema(title = "角色ID列表")
  @Size(min = 1, message = "角色ID列表不能为空")
  private List<Long> ids;
}
