package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门列表入参
 */
@Schema(title = "部门列表入参")
@Data
public class SysDeptListQO {

  @Schema(title = "名称")
  private String name;
  @Schema(title = "父级 ID")
  private Long parentId;
}
