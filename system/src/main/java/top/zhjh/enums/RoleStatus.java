package top.zhjh.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色状态枚举
 */
@Getter
@AllArgsConstructor
public enum RoleStatus {

  DISABLE(0, "禁用"),
  ENABLE(1, "启用"),
  ;

  @JsonValue
  @EnumValue
  private final Integer value;
  private final String desc;

  /**
   * value 是否在枚举中
   */
  public static boolean isInEnum(Object value) {
    for (RoleStatus roleStatus : RoleStatus.values()) {
      if ((value instanceof Integer && roleStatus.getValue().equals(value))
        || (value instanceof RoleStatus && roleStatus.getValue().equals(value))
      ) {
        return true;
      }
    }
    return false;
  }
}
