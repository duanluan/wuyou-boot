package top.zhjh.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseOptionVO implements Serializable {
  /**
   * 显示值
   */
  private String label;
  /**
   * 实际值
   */
  private String value;

}
