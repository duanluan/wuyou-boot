package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户更新入参
 */
@Schema(title = "用户更新入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserUpdateQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "ID错误")
  @NotNull(message = "ID不能为空")
  private Long id;
  @Schema(title = "昵称")
  @NotBlank(message = "昵称不能为空")
  private String nickName;
  @Schema(title = "角色列表")
  @Size(min = 1, message = "角色列表不能为空")
  private List<Long> roleIds;
  @Schema(title = "岗位列表")
  private List<Long> postIds;
}
