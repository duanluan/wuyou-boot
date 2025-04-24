package top.zhjh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseEntityNoTenant;

import java.util.List;

/**
 * 系统用户
 */
@Schema(title = "系统用户")
@TableName(
  // 必须开启映射注解，json 字段处理才能生效 https://baomidou.com/guides/type-handler/
  autoResultMap = true
)
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends BaseEntityNoTenant {

  @Schema(title = "用户名")
  private String username;
  @Schema(title = "密码")
  private String password;
  @Schema(title = "昵称")
  private String nickName;
  @Schema(title = "角色 ID 列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<Long> roleIds;
  @Schema(title = "角色名称列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<String> roleNames;
  @Schema(title = "角色编码列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<String> roleCodes;
  @Schema(title = "部门 ID 列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<Long> deptIds;
  @Schema(title = "部门名称列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<String> deptNames;
  @Schema(title = "岗位 ID 列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<Long> postIds;
  @Schema(title = "岗位名称列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<String> postNames;
  @Schema(title = "租户 ID 列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<Long> tenantIds;
  @Schema(title = "租户名称列表")
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<String> tenantNames;
}
