package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ${table.comment!} 服务实现
 *
<#if author != "">
  * @author ${author}
</#if>
<#if date != "">
 * @since ${date}
</#if>
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

  @Resource
  private ${table.mapperName} ${table.mapperName?substring(0, 1)?lower_case}${table.mapperName?substring(1)};

}
</#if>
