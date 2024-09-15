package top.zhjh.struct;

import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysMenuStruct {

  SysMenuStruct INSTANCE = Mappers.getMapper(SysMenuStruct.class);

  SysMenu to(SysMenuSaveQO obj);

  SysMenu to(SysMenuUpdateQO obj);
}
