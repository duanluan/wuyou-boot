package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 角色更新入参
 */
@Schema(title = "角色 更新入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleUpdateQO  {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色 ID 错误")
  private Long id;
  @Schema(title = "租户ID")
  @NotBlank(message = "租户ID不能为空")
  private String tenantId;
  @Schema(title = "创建人")
  @NotBlank(message = "创建人不能为空")
  private String createdBy;
  @Schema(title = "创建时间")
  @NotNull(message = "创建时间不能为空")
  private LocalDateTime createdTime;
  @Schema(title = "更新人")
  @NotBlank(message = "更新人不能为空")
  private String updatedBy;
  @Schema(title = "更新时间")
  @NotNull(message = "更新时间不能为空")
  private LocalDateTime updatedTime;
  @Schema(title = "编码")
  @NotBlank(message = "编码不能为空")
  private String code;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
}
