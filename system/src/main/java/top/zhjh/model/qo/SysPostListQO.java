package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;

/**
 * 岗位列表入参
 */
@Schema(title = "岗位列表入参")
@Data
public class SysPostListQO {

  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;
}
