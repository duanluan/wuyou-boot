package top.zhjh.model.qo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SysTestDataSaveQO {
  @NotBlank(message = "值不能为空")
  private String value;
  @NotNull(message = "部门不能为空")
  private Long deptId;
}