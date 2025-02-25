package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.MyPage;
import top.zhjh.enums.CommonStatus;
import top.zhjh.model.entity.SysDept;

/**
 * 岗位分页入参
 */
@Schema(title = "岗位分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysPostPageQO extends MyPage<SysDept> {

  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
  @Schema(title = "状态")
  private CommonStatus status;
}
