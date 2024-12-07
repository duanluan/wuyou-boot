package top.zhjh.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.DataScopeType;

/**
 * 角色
 */
@Schema(title = "角色")
@TableName("sys_role")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRole extends BaseEntity {
  /**
   * 编码
   */
  @Schema(title = "编码")
  private String code;
  /**
   * 名称
   */
  @Schema(title = "名称")
  private String name;
  /**
   * 描述
   */
  @Schema(title = "描述")
  private String description;
  /**
   * 顺序
   */
  @Schema(title = "顺序")
  private Long sort;
  /**
   * 状态
   */
  @Schema(title = "状态")
  private CommonStatus status;
  /**
   * 查询数据权限：1: 全部, 2: 自定义, 3: 本部门及以下, 4: 本部门, 5: 仅本人
   */
  @Schema(title = "查询数据权限：1: 全部, 2: 自定义, 3: 本部门及以下, 4: 本部门, 5: 仅本人")
  private DataScopeType queryDataScope;
  /**
   * 增删改数据权限：1: 全部, 2: 自定义, 3: 本部门及以下, 4: 本部门, 5: 仅本人
   */
  @Schema(title = "增删改数据权限：1: 全部, 2: 自定义, 3: 本部门及以下, 4: 本部门, 5: 仅本人")
  private DataScopeType updateDataScope;
}
