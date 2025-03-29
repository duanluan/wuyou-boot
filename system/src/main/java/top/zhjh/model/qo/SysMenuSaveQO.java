package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.enums.CommonStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 菜单保存入参
 */
@Schema(title = "菜单 保存入参")
@Data
public class SysMenuSaveQO  {

  @Schema(title = "父级 ID")
  @Min(value = 1, message = "父级错误")
  @NotBlank(message = "父级不能为空")
  private Long parentId;
  @Schema(title = "类型：1: 目录, 2: 菜单, 3: 按钮")
  private Integer type;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "图标")
  private String icon;
  @Schema(title = "请求路径")
  @NotBlank(message = "请求路径不能为空")
  private String path;
  @Schema(title = "请求方法：GET、HEAD、POST、PUT、PATCH、DELETE、OPTIONS、TRACE、CONNECT")
  private String method;
  @Schema(title = "权限标识")
  private String permission;
  @Schema(title = "是否需要登录")
  private Boolean needToLogin;
  @Schema(title = "顺序")
  private Integer sort;
  @Schema(title = "状态：0: 禁用, 1: 启用")
  private CommonStatus status;
}
