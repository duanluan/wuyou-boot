package top.zhjh.struct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.zhjh.model.entity.SysTenant;
import top.zhjh.model.qo.SysTenantSaveQO;
import top.zhjh.model.qo.SysTenantUpdateQO;

@Mapper
public interface SysTenantStruct {

  SysTenantStruct INSTANCE = Mappers.getMapper(SysTenantStruct.class);

  SysTenant to(SysTenantSaveQO obj);

  SysTenant to(SysTenantUpdateQO obj);
}
