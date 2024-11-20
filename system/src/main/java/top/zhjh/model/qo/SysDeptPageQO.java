package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.MyPage;
import top.zhjh.model.entity.SysDept;

/**
 * 部门分页入参
 */
@Schema(title = "SysDept 分页入参")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptPageQO extends MyPage<SysDept> {

  @Schema(title = "名称")
  private String name;
  @Schema(title = "父级 ID")
  private Long parentId;
}
