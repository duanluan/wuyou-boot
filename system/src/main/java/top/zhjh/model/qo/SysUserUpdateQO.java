package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户更新入参
 */
@Schema(title = "SysUser 更新入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserUpdateQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "用户ID错误")
  private Long id;
  @Schema(title = "昵称")
  @NotBlank(message = "昵称不能为空")
  private String nickName;
  @Schema(title = "角色ID列表")
  @Size(min = 1, message = "角色ID列表不能为空")
  private List<Long> roleIds;
}
