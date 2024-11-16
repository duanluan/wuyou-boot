package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.MyPage;
import top.zhjh.model.entity.SysMenu;

/**
 * 菜单分页入参
 */
@Schema(title = "菜单 分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuPageQO extends MyPage<SysMenu> {

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
  @Schema(title = "状态：1: 启用, 2: 禁用")
  private Integer status;
}
