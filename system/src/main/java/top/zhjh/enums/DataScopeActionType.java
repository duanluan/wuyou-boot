package top.zhjh.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum DataScopeActionType {

  QUERY(1, "查询"),
  UPDATE(2,"增删改"),
  ;

  @JsonValue
  @EnumValue
  private final Integer value;
  private final String desc;
}
