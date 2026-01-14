package top.zhjh.model.qo;
import lombok.Data;
import javax.validation.constraints.NotNull;
@Data
public class SysTestDataUpdateQO {
  @NotNull(message = "ID不能为空")
  private Long id;
  private String value;
}