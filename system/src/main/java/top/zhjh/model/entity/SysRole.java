package top.zhjh.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntity;

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
}
