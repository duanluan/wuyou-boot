package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户详情响应
 */
@Schema(title = "用户详情响应")
@Data
public class SysUserDetailVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "用户名")
  private String username;
  @Schema(title = "昵称")
  private String nickName;
  @Schema(title="创建时间")
  private String createdTime;
  @Schema(title = "角色名称列表")
  private List<String> roleNames;
  @Schema(title = "部门名称")
  private String deptName;
  @Schema(title = "岗位名称列表")
  private List<String> postNames;
}
