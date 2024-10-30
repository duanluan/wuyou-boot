package top.zhjh.base.model;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class BaseGetQO implements Serializable {

  @JSONField(serializeFeatures = {JSONWriter.Feature.WriteLongAsString})
  @Schema(title = "ID")
  @Min(value = 1, message = "ID 错误")
  private Long id;
}
