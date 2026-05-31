package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageVO;

import java.time.LocalDateTime;

/**
 * 字典项分页响应
 */
@Schema(title = "字典项分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictItemPageVO extends PageVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "字典ID")
  private Long dictId;
  @Schema(title = "字典标识")
  private String dictKey;
  @Schema(title = "字典项值")
  private String itemValue;
  @Schema(title = "字典项名称")
  private String label;
  @Schema(title = "描述")
  private String description;
  @Schema(title = "排序")
  private Long sortOrder;
  @Schema(title = "备注")
  private String remarks;
  @Schema(title = "是否系统内置")
  private Boolean systemBuiltIn;
  @Schema(title = "是否代码引用")
  private Boolean codeReferenced;
  @Schema(title = "基础字典项ID")
  private Long baseItemId;
  @Schema(title = "创建时间")
  private LocalDateTime createdTime;
}
