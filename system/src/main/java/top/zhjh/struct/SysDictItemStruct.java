package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.qo.SysDictItemListQO;
import top.zhjh.model.qo.SysDictItemPageQO;
import top.zhjh.model.qo.SysDictItemSaveQO;
import top.zhjh.model.qo.SysDictItemUpdateQO;

@Mapper
public interface SysDictItemStruct {

  SysDictItemStruct INSTANCE = Mappers.getMapper(SysDictItemStruct.class);

  SysDictItem to(SysDictItemSaveQO obj);

  SysDictItem to(SysDictItemUpdateQO obj);

  SysDictItemListQO to(SysDictItemPageQO query);
}
