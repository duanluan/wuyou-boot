package ${package.Entity}.qo;

<#if fileNameSuffix?contains('PageQO')>
import java.io.Serializable;
<#elseif fileNameSuffix?contains('GetQO')>
import top.zhjh.base.model.BaseGetQO;
</#if>
import ${package.Entity}.${entity};
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

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ${table.comment!}<#if fileNameSuffix?contains('Page')>分页<#elseif fileNameSuffix?contains('Get')>详情<#elseif fileNameSuffix?contains('Save')>保存<#elseif fileNameSuffix?contains('Update')>更新<#elseif fileNameSuffix?contains('Remove')>删除<#else></#if>入参
 *
<#if author != "">
 * @author ${author}
</#if>
<#if date != "">
 * @since ${date}
</#if>
 */
<#if swagger??>
@Schema(title = "${table.comment!} <#if fileNameSuffix?contains('Page')>分页入参<#elseif fileNameSuffix?contains('Get')>详情入参<#elseif fileNameSuffix?contains('Save')>保存入参<#elseif fileNameSuffix?contains('Update')>更新入参<#elseif fileNameSuffix?contains('Remove')>删除入参<#else></#if>")
</#if>
<#if entityLombokModel>
  <#if fileNameSuffix?contains('PageQO')>
@EqualsAndHashCode(callSuper = true)
  <#else>
@EqualsAndHashCode(callSuper = false)
  </#if>
  <#if chainModel>
@Accessors(chain = true)
  </#if>
@Data
</#if>
<#if fileNameSuffix?contains("PageQO")>
public class ${entity}${fileNameSuffix} implements Serializable {
<#elseif fileNameSuffix?contains("GetQO")>
public class ${entity}${fileNameSuffix} extends BaseGetQO {
<#else>
public class ${entity}${fileNameSuffix} <#if entitySerialVersionUID>implements Serializable</#if> {
</#if>

<#if entitySerialVersionUID>
  private static final long serialVersionUID = 1L;

</#if>
<#-- 如果包含是删除，添加多个 ID 的字段 -->
<#if fileNameSuffix?contains('Remove')>
  @Schema(title = "多个${table.comment!} ID，逗号分隔")
  private String ids;
</#if>
<#-- 临时写法 -->
<#if fileNameSuffix?contains('Get') || fileNameSuffix?contains('Update')>
  @Schema(title = "ID")
  @Min(value = 1, message = "${table.comment!} ID 错误")
  private Long id;
</#if>
<#-- ----------  BEGIN 字段循环遍历  -------- -->
<#list table.fields as field>
  <#-- 排除公共字段 -->
  <#if (fileNameSuffix?contains('Get') && field.propertyName=='id') || (fileNameSuffix?contains('Update') && field.propertyName!='createdTime') || (field.propertyName!='tenantId'&&field.propertyName!='createdBy'&&field.propertyName!='updatedBy'&&field.propertyName!='updatedTime')>
  <#if field.keyFlag>
    <#assign keyPropertyName="${field.propertyName}"/>
  </#if>
  <#if field.comment!?length gt 0 && swagger??>
  @Schema(title = "${field.comment}")
  </#if>
  <#-- 校验 -->
  <#if field.propertyType == 'Long'>
  @NotNull(message = "${field.comment}不能为空")
  @Min(value = 1, message = "${field.comment} 错误")
  <#elseif field.propertyType == 'String'>
  @NotBlank(message = "${field.comment}不能为空")
  <#elseif field.propertyType == 'LocalDateTime'>
  @NotNull(message = "${field.comment}不能为空")
  <#else>
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
