package top.zhjh.mapper;

import lombok.NonNull;
import org.apache.ibatis.annotations.Param;
import top.zhjh.model.entity.SysUser;
import top.zhjh.model.qo.SysUserListQO;
import top.zhjh.model.qo.SysUserPageQO;
import top.zhjh.model.vo.SysUserDetailVO;
import top.zhjh.model.vo.SysUserPageVO;
import top.zhjh.mybatis.MyBaseMapper;

import java.util.List;

/**
 * 用户 Mapper
 */
public interface SysUserMapper extends MyBaseMapper<SysUser> {

  List<String> listRoleCodes(@NonNull @Param("id") Long id);

  List<String> listPermission(@NonNull @Param("id") Long id);

  List<SysUserPageVO> list(SysUserListQO query);

  List<SysUserPageVO> page(SysUserPageQO query);

  SysUserDetailVO getDetail(@NonNull @Param("id") Long id);

  int updateById(SysUser obj);
}
