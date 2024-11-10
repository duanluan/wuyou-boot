package ${package.Controller};

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ${package.Parent}.base.model.PageVO;
import ${package.Parent}.base.model.R;
import ${package.Entity}.${entity};
import ${package.Entity?substring(0, package.Entity?last_index_of("."))}.qo.*;
import ${package.Entity?substring(0, package.Entity?last_index_of("."))}.vo.*;
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${package.Service}.${table.serviceName};
import ${package.Parent}.struct.${entity}Struct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * ${table.comment!} 控制器
 *
<#if author != "">
 * @author ${author}
</#if>
<#if date != "">
 * @since ${date}
</#if>
 */
@Tag(name = "${table.comment!}")
@Slf4j
@RequestMapping("<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

  @Resource
  private ${table.serviceName} ${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)};

  @Operation(summary = "${table.comment!}列表")
  @GetMapping
  public R<List<${entity}>> list(@Validated ${entity}PageQO query) {
    LambdaQueryWrapper<${entity}> queryWrapper = new LambdaQueryWrapper<${entity}>().orderByDesc(${entity}::getId);
    if (query.getCurrent() == 0) {
      return ok(${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)}.list(queryWrapper));
    }
    return ok(${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)}.page(query, queryWrapper));
  }

  @Operation(summary = "${table.comment!}详情")
  @GetMapping("/{id}")
  public R<${entity}GetVO> get(@Validated ${entity}GetQO query) {
    return ok(${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)}.getById(query.getId()));
  }

  @Operation(summary = "保存${table.comment!}")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R save(@RequestBody @Validated ${entity}SaveQO obj) {
    return custom(${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)}.save(${entity}Struct.INSTANCE.to(obj)), SAVE_FAILED_MSG);
  }

  @Operation(summary = "更新${table.comment!}")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R update(@RequestBody @Validated ${entity}UpdateQO obj) {
    return custom(${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)}.updateById(${entity}Struct.INSTANCE.to(obj)), UPDATE_FAILED_MSG);
  }

  @Operation(summary = "删除${table.comment!}")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R remove(@Validated ${entity}RemoveQO query) {
    return custom(${table.serviceName?substring(1, 2)?lower_case}${table.serviceName?substring(2)}.removeByIds(Arrays.asList(query.getIds().split(","))), REMOVE_FAILED_MSG);
  }
}
</#if>
