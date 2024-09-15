package top.zhjh.model.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.BaseGetQO;

/**
 * 用户详情入参
 */
@Schema(title = "SysUser 详情入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class SysUserGetQO extends BaseGetQO {
}
