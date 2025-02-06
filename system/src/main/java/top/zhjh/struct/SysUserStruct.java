package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysUser;
import top.zhjh.model.qo.SysUserListQO;
import top.zhjh.model.qo.SysUserPageQO;
import top.zhjh.model.qo.SysUserSaveQO;
import top.zhjh.model.qo.SysUserUpdateQO;
import top.zhjh.model.vo.SysUserDetailVO;

@Mapper
public interface SysUserStruct {

  SysUserStruct INSTANCE = Mappers.getMapper(SysUserStruct.class);

  SysUser to(SysUserSaveQO obj);

  SysUser to(SysUserUpdateQO obj);

  SysUserDetailVO toDetailVO(SysUser obj);

  SysUserListQO to(SysUserPageQO query);
}
