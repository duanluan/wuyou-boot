package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};

/**
 * ${table.comment!} Mapper
 *
<#if author != "">
 * @author ${author}
</#if>
<#if date != "">
 * @since ${date}
</#if>
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

}
</#if>