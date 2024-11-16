package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户分页响应
 */
@Schema(title = "用户分页响应")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserPageVO extends PageVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "用户名")
  private String username;
  @Schema(title = "昵称")
  private String nickName;
  @Schema(title = "角色ID列表")
  private List<Long> roleIds;
  @Schema(title = "角色名称列表")
  private List<String> roleNames;
  @Schema(title = "创建时间")
  private LocalDateTime createdTime;
}
