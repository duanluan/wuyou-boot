package top.zhjh.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 */
@AllArgsConstructor
@Getter
public enum MenuType {
  /**
   * 目录
   */
  DIR(1),
  /**
   * 菜单
   */
  MENU(2),
  /**
   * 按钮
   */
  BTN(3),
  ;

  @JsonValue
  @EnumValue
  private final Integer value;
}
