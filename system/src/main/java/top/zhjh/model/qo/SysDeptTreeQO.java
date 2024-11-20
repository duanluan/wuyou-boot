package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;

/**
 * 部门树入参
 */
@Data
public class SysDeptTreeQO {
  @Schema(title = "是否不构建树")
  private Boolean notBuildTree;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;
}
