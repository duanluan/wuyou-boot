package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysTestDataPageVO {
  @Schema(title = "ID")
  private Long id;

  @Schema(title = "值")
  private String value;

  @Schema(title = "部门ID")
  private Long deptId;

  @Schema(title = "部门名称")
  private String deptName;

  @Schema(title = "创建人ID")
  private Long createdBy;

  @Schema(title = "创建人名称")
  private String creatorName;

  @Schema(title = "创建时间")
  private LocalDateTime createdTime;
}