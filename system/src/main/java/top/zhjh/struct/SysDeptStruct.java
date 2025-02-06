package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysDept;
import top.zhjh.model.qo.SysDeptListQO;
import top.zhjh.model.qo.SysDeptPageQO;
import top.zhjh.model.qo.SysDeptSaveQO;
import top.zhjh.model.qo.SysDeptUpdateQO;

@Mapper
public interface SysDeptStruct {

  SysDeptStruct INSTANCE = Mappers.getMapper(SysDeptStruct.class);

  SysDept to(SysDeptSaveQO obj);

  SysDept to(SysDeptUpdateQO obj);

  SysDeptListQO to(SysDeptPageQO query);
}
