package top.zhjh.config.dict;

import lombok.Builder;
import lombok.Getter;

/**
 * 系统字典项定义
 */
@Getter
@Builder
public class SystemDictItemDefinition {

  private final String itemValue;
  private final String defaultLabel;
  private final String defaultDescription;
  private final long defaultSortOrder;
  private final boolean systemBuiltIn;
  private final boolean codeReferenced;
}
