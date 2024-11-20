package top.zhjh.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class BaseGetQO implements Serializable {

  @Schema(title = "ID")
  @Min(value = 1, message = "ID错误")
  private Long id;
}
