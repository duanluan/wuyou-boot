package top.zhjh.model.vo;

import top.zhjh.base.model.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色响应
 *
 * @author ZhongJianhao
 */
@Schema(title = "角色 分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageVO extends PageVO {

  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
}
