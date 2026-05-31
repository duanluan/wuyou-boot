package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysDict;
import top.zhjh.model.qo.SysDictListQO;
import top.zhjh.model.qo.SysDictPageQO;
import top.zhjh.model.qo.SysDictSaveQO;
import top.zhjh.model.qo.SysDictUpdateQO;

@Mapper
public interface SysDictStruct {

  SysDictStruct INSTANCE = Mappers.getMapper(SysDictStruct.class);

  SysDict to(SysDictSaveQO obj);

  SysDict to(SysDictUpdateQO obj);

  SysDictListQO to(SysDictPageQO query);
}
