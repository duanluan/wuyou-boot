package top.zhjh.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典来源范围
 */
@Getter
@AllArgsConstructor
public enum DictSourceScope {

  PUBLIC_SYSTEM("PUBLIC_SYSTEM"),
  TENANT_OVERRIDE("TENANT_OVERRIDE"),
  TENANT_CUSTOM("TENANT_CUSTOM"),
  ;

  @JsonValue
  @EnumValue
  private final String value;
}
