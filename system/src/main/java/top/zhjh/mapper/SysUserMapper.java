package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.zhjh.base.model.MyPage;
import top.zhjh.model.entity.SysUser;
import top.zhjh.model.qo.SysUserListQO;
import top.zhjh.model.qo.SysUserPageQO;
import top.zhjh.model.vo.SysUserPageVO;

import java.util.List;

/**
 * 用户 Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

  List<String> listRoleCodes(@Param("id") Long id);

  List<String> listPermission(@Param("id") Long id);

  List<SysUserPageVO> list(SysUserListQO query);

  MyPage<SysUserPageVO> page(SysUserPageQO query);
}
