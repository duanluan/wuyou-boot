package top.zhjh.struct;

import top.zhjh.model.entity.SysRole;
import top.zhjh.model.qo.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysRoleStruct {

  SysRoleStruct INSTANCE = Mappers.getMapper(SysRoleStruct.class);

  SysRole to(SysRoleSaveQO obj);

  SysRole to(SysRoleUpdateQO obj);

  SysRole to(SysRoleUpdateStatusQO obj);

  SysRole to(SysRoleUpdateDataScopeQO obj);
}
