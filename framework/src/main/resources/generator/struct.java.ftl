package ${package.Parent};

import ${package.Entity}.${entity};
import ${package.Entity?substring(0, package.Entity?last_index_of("."))}.qo.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ${entity}Struct {

  ${entity}Struct INSTANCE = Mappers.getMapper(${entity}Struct.class);

  ${entity} to(${entity}SaveQO obj);

  ${entity} to(${entity}UpdateQO obj);
}
