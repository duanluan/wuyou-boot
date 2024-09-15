package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户详情响应
 */
@Schema(title = "用户详情响应")
@Data
public class SysUserDetailVO {

  @Schema(title = "ID")
  private Long id;
  @Schema(title = "创建人")
  private String createdBy;
  @Schema(title = "创建时间")
  private Date createdTime;
  @Schema(title = "更新人")
  private String updatedBy;
  @Schema(title = "更新时间")
  private Date updatedTime;
  @Schema(title = "用户名")
  private String username;
  @Schema(title = "昵称")
  private String nickName;
}
