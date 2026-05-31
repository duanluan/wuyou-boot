package top.zhjh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntityNoDept;

/**
 * 字典项
 */
@Schema(title = "字典项")
@TableName("sys_dict_item")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictItem extends BaseEntityNoDept {

  @Schema(title = "字典ID")
  @TableField("dict_id")
  private Long dictId;
  @Schema(title = "字典标识")
  @TableField("dict_key")
  private String dictKey;
  @Schema(title = "字典项值")
  @TableField("item_value")
  private String itemValue;
  @Schema(title = "字典项名称")
  private String label;
  @Schema(title = "描述")
  private String description;
  @Schema(title = "排序")
  @TableField("sort_order")
  private Long sortOrder;
  @Schema(title = "备注")
  private String remarks;
  @Schema(title = "是否系统内置")
  private Boolean systemBuiltIn;
  @Schema(title = "是否代码引用")
  private Boolean codeReferenced;
  @Schema(title = "基础字典项ID")
  private Long baseItemId;
}
