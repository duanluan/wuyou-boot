package top.zhjh.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限枚举
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {

  ALL(1, "全部"),
  CUSTOM(2, "自定义"),
  CURRENT_DEPT_AND_CHILDREN(3, "本部门及以下"),
  CURRENT_DEPT(4, "本部门"),
  ONLY_SELF(5, "仅本人");

  @JsonValue
  @EnumValue
  private final Integer value;
  private final String desc;
}
