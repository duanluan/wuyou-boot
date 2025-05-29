package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageVO;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.DataScopeType;

import java.time.LocalDateTime;

@Schema(title = "角色分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageVO extends PageVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "描述")
  private String description;
  @Schema(title = "顺序")
  private Long sort;
  @Schema(title = "状态")
  private CommonStatus status;
  @Schema(title = "租户名称")
  private String tenantName;
  @Schema(title = "创建时间")
  private LocalDateTime createdTime;
  @Schema(title = "查询数据权限")
  private DataScopeType queryDataScope;
  @Schema(title = "增删改数据权限")
  private DataScopeType updateDataScope;
}
