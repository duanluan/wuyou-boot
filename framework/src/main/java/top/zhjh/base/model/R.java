package top.zhjh.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(title = "响应结果")
@Data
public class R<T> {

  @Schema(title = "编码")
  private int code;
  @Schema(title = "消息")
  private String msg;
  @Schema(title = "数据")
  private T data;

  @Schema(title = "当前页码")
  private Long current;
  @Schema(title = "每页数量")
  private Long size;
  @Schema(title = "总条数")
  private Long total;

  public R() {
  }

  public R(int code) {
    this.code = code;
  }

  public R(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public R(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }
}
