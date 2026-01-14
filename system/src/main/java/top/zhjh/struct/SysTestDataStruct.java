package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysTestData;
import top.zhjh.model.qo.*;

@Mapper
public interface SysTestDataStruct {

  SysTestDataStruct INSTANCE = Mappers.getMapper(SysTestDataStruct.class);

  SysTestData to(SysTestDataSaveQO obj);

  SysTestData to(SysTestDataUpdateQO obj);
}
