package top.zhjh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntityNoDept;
import top.zhjh.enums.DictSourceScope;

/**
 * 字典
 */
@Schema(title = "字典")
@TableName("sys_dict")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDict extends BaseEntityNoDept {

  @Schema(title = "字典名称")
  private String name;
  @Schema(title = "字典标识")
  @TableField("`key`")
  private String key;
  @Schema(title = "备注")
  private String remarks;
  @Schema(title = "是否系统内置")
  private Boolean systemBuiltIn;
  @Schema(title = "是否代码引用")
  private Boolean codeReferenced;
  @Schema(title = "是否允许租户覆盖")
  private Boolean allowTenantOverride;
  @Schema(title = "来源范围")
  private DictSourceScope sourceScope;
  @Schema(title = "基础字典ID")
  private Long baseDictId;
}
