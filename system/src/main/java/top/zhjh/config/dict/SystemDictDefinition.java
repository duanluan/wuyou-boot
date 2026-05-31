package top.zhjh.config.dict;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 系统字典定义
 */
@Getter
@Builder
public class SystemDictDefinition {

  private final String dictKey;
  private final String defaultName;
  private final String defaultRemarks;
  private final boolean systemBuiltIn;
  private final boolean codeReferenced;
  private final boolean allowTenantOverride;
  private final List<SystemDictItemDefinition> items;
}
