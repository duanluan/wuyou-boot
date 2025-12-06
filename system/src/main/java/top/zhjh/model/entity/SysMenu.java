package top.zhjh.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntityNoDept;
import top.zhjh.enums.CommonStatus;

/**
 * 菜单
 */
@Schema(title = "菜单")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenu extends BaseEntityNoDept {

  /**
   * 父级 ID
   */
  @Schema(title = "父级 ID")
  private Long parentId;
  /**
   * 类型：1: 目录, 2: 菜单, 3: 按钮
   */
  @Schema(title = "类型：1: 目录, 2: 菜单, 3: 按钮")
  private Integer type;
  /**
   * 名称
   */
  @Schema(title = "名称")
  private String name;
  /**
   * 图标
   */
  @Schema(title = "图标")
  private String icon;
  /**
   * 请求路径
   */
  @Schema(title = "请求路径")
  private String path;
  /**
   * 请求方法：GET、HEAD、POST、PUT、PATCH、DELETE、OPTIONS、TRACE、CONNECT
   */
  @Schema(title = "请求方法：GET、HEAD、POST、PUT、PATCH、DELETE、OPTIONS、TRACE、CONNECT")
  private String method;
  /**
   * 权限标识
   */
  @Schema(title = "权限标识")
  private String permission;
  /**
   * 是否需要登录
   */
  @Schema(title = "是否需要登录")
  private Boolean needToLogin;
  /**
   * 顺序
   */
  @Schema(title = "顺序")
  private Integer sort;
  /**
   * 状态：0: 禁用, 1: 启用
   */
  @Schema(title = "状态：0: 禁用, 1: 启用")
  private CommonStatus status;
}
