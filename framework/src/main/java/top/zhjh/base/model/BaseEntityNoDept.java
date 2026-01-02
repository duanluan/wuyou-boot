package top.zhjh.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 基础实体，没有数据权限
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntityNoDept extends BaseEntityNoTenant implements Serializable {

  @Schema(title = "租户 ID")
  @TableField(fill = FieldFill.INSERT)
  private Long tenantId;
}
