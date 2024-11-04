package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 角色删除入参
 *
 * @author ZhongJianhao
 */
@Schema(title = "角色 删除入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysRoleRemoveQO {

  @Schema(title = "多个角色ID，逗号分隔")
  @NotBlank(message = "多个角色ID不能为空")
  private String ids;
}
