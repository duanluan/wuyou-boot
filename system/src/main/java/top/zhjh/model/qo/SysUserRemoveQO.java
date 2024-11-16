package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户删除入参
 */
@Schema(title = "SysUser 删除入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserRemoveQO  {

  @Schema(title = "多个用户 ID，逗号分隔")
  private String ids;
}
