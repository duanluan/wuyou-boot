package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户删除入参
 */
@Schema(title = "用户删除入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserRemoveQO {

  @Schema(title = "用户ID列表")
  @Size(min = 1, message = "删除目标不能为空")
  @NotNull(message = "删除目标不能为空")
  private List<Long> ids;
}
