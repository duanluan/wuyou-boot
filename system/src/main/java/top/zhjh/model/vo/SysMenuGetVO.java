package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntityNoDept;
import top.zhjh.enums.CommonStatus;

/**
 * 菜单详情响应
 */
@Schema(title = "菜单 详情响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuGetVO extends BaseEntityNoDept {

  @Schema(title = "父级 ID")
  private Long parentId;
  @Schema(title = "类型：1: 目录, 2: 菜单, 3: 按钮")
  private Boolean type;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "图标")
  private String icon;
  @Schema(title = "请求路径")
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
