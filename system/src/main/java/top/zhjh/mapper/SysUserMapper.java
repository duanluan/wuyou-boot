package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.zhjh.model.entity.SysUser;

import java.util.List;

/**
 * 用户 Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

  List<String> listRoleCodes(@Param("id") String id);

  List<String> listPermission(@Param("id") String id);
}
