package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageVO;
import top.zhjh.enums.DictSourceScope;

import java.time.LocalDateTime;

/**
 * 字典分页响应
 */
@Schema(title = "字典分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictPageVO extends PageVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "租户ID")
  private Long tenantId;
  @Schema(title = "租户名称")
  private String tenantName;
  @Schema(title = "字典名称")
  private String name;
  @Schema(title = "字典标识")
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
  @Schema(title = "创建时间")
  private LocalDateTime createdTime;
}
