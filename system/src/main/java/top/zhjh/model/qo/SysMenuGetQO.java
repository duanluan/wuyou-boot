package top.zhjh.model.qo;

import top.zhjh.base.model.BaseGetQO;
import top.zhjh.enums.CommonStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 菜单详情入参
 */
@Schema(title = "菜单 详情入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysMenuGetQO extends BaseGetQO {

  @Schema(title = "ID")
  @Min(value = 1, message = "菜单ID错误")
  private Long id;
  @Schema(title = "创建时间")
  @NotNull(message = "创建时间不能为空")
  private LocalDateTime createdTime;
  @Schema(title = "父级 ID")
  @Min(value = 0, message = "父级ID错误")
  @NotNull(message = "父级ID不能为空")
  private Long parentId;
  @Schema(title = "类型：1: 目录, 2: 菜单, 3: 按钮")
  private Boolean type;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空")
  private String name;
  @Schema(title = "图标")
  @NotBlank(message = "图标不能为空")
  private String icon;
  @Schema(title = "请求路径")
  @NotBlank(message = "请求路径不能为空")
  private String path;
  @Schema(title = "请求方法：GET、HEAD、POST、PUT、PATCH、DELETE、OPTIONS、TRACE、CONNECT")
  @NotBlank(message = "请求方法：GET、HEAD、POST、PUT、PATCH、DELETE、OPTIONS、TRACE、CONNECT不能为空")
  private String method;
  @Schema(title = "权限标识")
  @NotBlank(message = "权限标识不能为空")
  private String permission;
  @Schema(title = "是否需要登录")
  private Boolean needToLogin;
  @Schema(title = "顺序")
  private Integer sort;
  @Schema(title = "状态：0: 禁用, 1: 启用")
  private CommonStatus status;
}
