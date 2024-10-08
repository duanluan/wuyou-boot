package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 角色保存入参
 *
 * @author ZhongJianhao
 */
@Schema(title = "角色 保存入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleSaveQO  {

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
