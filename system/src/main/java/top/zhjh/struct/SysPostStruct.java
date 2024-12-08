package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysPost;
import top.zhjh.model.qo.SysPostSaveQO;
import top.zhjh.model.qo.SysPostUpdateQO;

@Mapper
public interface SysPostStruct {

  SysPostStruct INSTANCE = Mappers.getMapper(SysPostStruct.class);

  SysPost to(SysPostSaveQO obj);

  SysPost to(SysPostUpdateQO obj);
}
