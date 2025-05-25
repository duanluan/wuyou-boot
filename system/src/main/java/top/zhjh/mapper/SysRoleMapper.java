package top.zhjh.mapper;

import top.zhjh.model.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.zhjh.model.qo.SysRoleListQO;
import top.zhjh.model.qo.SysRolePageQO;
import top.zhjh.model.vo.SysRolePageVO;

import java.util.List;

/**
 * 角色 Mapper
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

  List<SysRolePageVO> list(SysRoleListQO query);

  List<SysRolePageVO> page(SysRolePageQO query);
}
