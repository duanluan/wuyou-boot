package top.zhjh.model.qo;

import top.zhjh.base.model.BaseGetQO;
import top.zhjh.model.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 角色详情入参
 */
@Schema(title = "角色 详情入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleGetQO extends BaseGetQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "角色 ID 错误")
  private Long id;
  @Schema(title = "创建时间")
  @NotNull(message = "创建时间不能为空")
  private LocalDateTime createdTime;
  @Schema(title = "编码")
  @NotBlank(message = "编码不能为空")
  private String code;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
}
