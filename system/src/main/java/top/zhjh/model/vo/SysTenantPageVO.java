package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageVO;
import top.zhjh.enums.CommonStatus;

import java.time.LocalDateTime;

/**
 * 租户分页响应
 */
@Schema(title = "租户分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantPageVO extends PageVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "顺序")
  private Long sort;
  @Schema(title = "状态")
  private CommonStatus status;
  @Schema(title = "创建时间")
  private LocalDateTime createdTime;
}
