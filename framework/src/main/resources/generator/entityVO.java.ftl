package ${package.Entity}.vo;

<#if fileNameSuffix?contains('PageVO')>
import top.zhjh.base.model.PageVO;
<#elseif fileNameSuffix?contains('GetVO')>
import top.zhjh.base.model.BaseEntity;
</#if>
<#--<#list table.importPackages as pkg>
import ${pkg};
</#list>-->
<#if swagger??>
import io.swagger.v3.oas.annotations.media.Schema;
</#if>
<#if entityLombokModel>
import lombok.Data;
import lombok.EqualsAndHashCode;
  <#if chainModel>
import lombok.experimental.Accessors;
  </#if>
</#if>

/**
 * ${table.comment!}<#if fileNameSuffix?contains('List')>分页<#elseif fileNameSuffix?contains('Get')>详情<#else></#if>响应
 *
<#if author != "">
 * @author ${author}
</#if>
<#if date != "">
 * @since ${date}
</#if>
 */
<#if swagger??>
@Schema(title = "${table.comment!} <#if fileNameSuffix?contains('Page')>分页响应<#elseif fileNameSuffix?contains('Get')>详情响应<#else></#if>")
</#if>
<#if entityLombokModel>
  <#if fileNameSuffix?contains('PageVO') || fileNameSuffix?contains('GetVO')>
@EqualsAndHashCode(callSuper = true)
  <#else>
@EqualsAndHashCode(callSuper = false)
  </#if>
  <#if chainModel>
@Accessors(chain = true)
  </#if>
@Data
</#if>
<#if fileNameSuffix?contains("PageVO")>
public class ${entity + fileNameSuffix} extends PageVO {
<#elseif fileNameSuffix?contains('GetVO')>
public class ${entity + fileNameSuffix} extends BaseEntityNoDept {
<#else>
public class ${entity + fileNameSuffix} <#if entitySerialVersionUID>implements Serializable</#if> {
</#if>

<#if entitySerialVersionUID>
  private static final long serialVersionUID = 1L;

</#if>
<#-- ----------  BEGIN 字段循环遍历  -------- -->
<#list table.fields as field>
  <#-- 排除公共字段 -->
  <#if field.propertyName != 'id' && field.propertyName != 'tenantId' && field.propertyName != 'deleted' && field.propertyName != 'createdTime' && field.propertyName != 'createdBy' && field.propertyName != 'updatedTime' && field.propertyName != 'updatedBy' && field.propertyName != 'remark'>
  <#if field.keyFlag>
    <#assign keyPropertyName="${field.propertyName}"/>
  </#if>
  <#if field.comment!?length gt 0 && swagger??>
  @Schema(title = "${field.comment}")
  </#if>
  <#if field.keyFlag>
    <#-- 主键 -->
    <#if field.keyIdentityFlag>
  @TableId(value = "${field.annotationColumnName}", type = IdType.AUTO)
    <#elseif idType??>
  @TableId(value = "${field.annotationColumnName}", type = IdType.${idType})
    <#elseif field.convert>
  @TableId("${field.annotationColumnName}")
    </#if>
    <#-- 普通字段 -->
  <#elseif field.fill??>
  <#-- -----   存在字段填充设置   ----->
    <#if field.convert>
  @TableField(value = "${field.annotationColumnName}", fill = FieldFill.${field.fill})
    <#else>
  @TableField(fill = FieldFill.${field.fill})
    </#if>
  <#elseif field.convert>
  @TableField("${field.annotationColumnName}")
  </#if>
  <#-- 乐观锁注解 -->
  <#if field.versionField>
  @Version
  </#if>
  <#-- 逻辑删除注解 -->
  <#if field.logicDeleteField>
  @TableLogic
  </#if>
  private ${field.propertyType} ${field.propertyName};
  </#if>
</#list>
<#-- ----------  END 字段循环遍历  -------- -->
<#-- Lombok 模式 -->
<#if !entityLombokModel>
  <#list table.fields as field>
    <#if field.propertyType == "boolean">
      <#assign getprefix="is"/>
    <#else>
      <#assign getprefix="get"/>
    </#if>
  public ${field.propertyType} ${getprefix}${field.capitalName}() {
    return ${field.propertyName};
  }

  <#if chainModel>
  public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
  <#else>
  public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
  </#if>
    this.${field.propertyName} = ${field.propertyName};
    <#if chainModel>
    return this;
    </#if>
  }
  </#list>
</#if>
<#-- 列常量 -->
<#if entityColumnConstant>
  <#list table.fields as field>
  public static final String ${field.name?upper_case} = "${field.name}";

  </#list>
</#if>
<#if activeRecord>
  @Override
  protected Serializable pkVal() {
  <#if keyPropertyName??>
    return this.${keyPropertyName};
  <#else>
    return null;
  </#if>
  }

</#if>
<#if !entityLombokModel>
  @Override
  public String toString() {
    return "${entity}{" +
  <#list table.fields as field>
    <#if field_index==0>
      "${field.propertyName}=" + ${field.propertyName} +
    <#else>
      ", ${field.propertyName}=" + ${field.propertyName} +
    </#if>
  </#list>
    "}";
  }
</#if>
}
