package top.zhjh.model.qo;

import top.zhjh.base.model.BasePageQO;
import top.zhjh.model.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 角色分页入参
 *
 * @author ZhongJianhao
 */
@Schema(title = "角色 分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageQO extends BasePageQO<SysRole> {

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
