package top.zhjh.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体
 */
@Data
public class BaseEntity implements Serializable {
  @Schema(title = "ID")
  private Long id;
  @Schema(title = "租户 ID")
  private Long tenantId;
  @Schema(title = "乐观锁")
  private Integer revision;
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "创建人")
  private Long createdBy;
  @Schema(title = "创建时间")
  private Date createdTime;
  @TableField(fill = FieldFill.UPDATE)
  @Schema(title = "更新人")
  private Long updatedBy;
  @Schema(title = "更新时间")
  private Date updatedTime;
  @Schema(title = "是否删除")
  private Boolean deleted;
}
