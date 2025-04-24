package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.zhjh.enums.CommonStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SysDeptUpdateQO {

  public interface UpdateGroup {
  }

  public interface UpdateStatusGroup {
  }

  @Schema(title = "ID")
  @Min(value = 1, message = "部门ID错误", groups = {UpdateGroup.class, UpdateStatusGroup.class})
  private Long id;
  @Schema(title = "名称")
  @NotBlank(message = "名称不能为空", groups = {UpdateGroup.class})
  private String name;
  @Schema(title = "父级 ID")
  @Min(value = 0, message = "父级ID错误", groups = {UpdateGroup.class})
  private Long parentId;
  @Schema(title = "顺序")
  @Min(value = 1, message = "顺序错误", groups = {UpdateGroup.class})
  private Long sort;
  @Schema(title = "状态")
  @NotNull(message = "状态不能为空", groups = {UpdateGroup.class, UpdateStatusGroup.class})
  private CommonStatus status;
}
