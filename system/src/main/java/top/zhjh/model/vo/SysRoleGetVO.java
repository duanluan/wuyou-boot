package top.zhjh.model.vo;

import top.zhjh.base.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色详情响应
 *
 * @author ZhongJianhao
 */
@Schema(title = "角色 详情响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleGetVO extends BaseEntity {

  @Schema(title = "编码")
  private String code;
  @Schema(title = "名称")
  private String name;
}
