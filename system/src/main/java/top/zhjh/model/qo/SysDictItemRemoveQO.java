package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 字典项删除入参
 */
@Schema(title = "字典项删除入参")
@Data
public class SysDictItemRemoveQO {

  @Schema(title = "字典项ID列表")
  @Size(min = 1, message = "删除目标不能为空")
  @NotNull(message = "删除目标不能为空")
  private List<Long> ids;
}
