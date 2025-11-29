package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单更新入参
 */
@Schema(title = "菜单 更新入参")
@Data
public class SysMenuUpdateQO {

  public interface UpdateGroup {
  }

  public interface UpdateStatusGroup {
  }

  @Schema(title = "ID")
  @Min(value = 1, message = "ID错误")
  @NotNull(message = "ID不能为空")
  private Long id;
  @Schema(title = "父级 ID")
  @Min(value = 0, message = "父级错误", groups = {UpdateGroup.class})
  @NotNull(message = "父级不能为空", groups = {UpdateGroup.class})
  private Long parentId;
  @Schema(title = "类型")
  @NotNull(message = "类型不能为空", groups = {UpdateGroup.class})
  private Integer type;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空", groups = {UpdateGroup.class})
  private String name;
  @Schema(title = "图标")
  private String icon;
  @Schema(title = "请求路径")
  private String path;
  @Schema(title = "请求方法")
  private String method;
  @Schema(title = "权限标识")
  private String permission;
  @Schema(title = "是否需要登录")
  private Boolean needToLogin;
  @Schema(title = "顺序")
  @Min(value = 1, message = "顺序错误", groups = {UpdateGroup.class})
  @NotNull(message = "顺序不能为空", groups = {UpdateGroup.class})
  private Integer sort;
  @Schema(title = "状态")
  @NotNull(message = "状态不能为空", groups = {UpdateGroup.class, UpdateStatusGroup.class})
  private CommonStatus status;
}
