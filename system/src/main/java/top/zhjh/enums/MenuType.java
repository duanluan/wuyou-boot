package top.zhjh.enums;

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

  private final Integer code;
}
