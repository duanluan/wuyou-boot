package top.zhjh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.qo.SysTestDataPageQO;
import top.zhjh.model.qo.SysTestDataSaveQO;
import top.zhjh.model.qo.SysTestDataUpdateQO;
import top.zhjh.service.SysTestDataService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "测试数据用于测试数据权限")
@RequestMapping("/sys/testData")
@RestController
public class SysTestDataController extends BaseController {

  @Resource
  private SysTestDataService sysTestDataService;

  @Operation(summary = "测试数据列表")
  @GetMapping
  // @SaCheckPermission("sys:testData:list") // 暂时注释，方便你直接测试
  public R<?> list(@Validated SysTestDataPageQO query) {
    return ok(sysTestDataService.page(query));
  }

  @Operation(summary = "保存测试数据")
  @PostMapping
  public R<?> save(@RequestBody @Validated SysTestDataSaveQO obj) {
    return saveR(sysTestDataService.save(obj));
  }

  @Operation(summary = "更新测试数据")
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated SysTestDataUpdateQO obj) {
    return updateR(sysTestDataService.updateById(obj));
  }

  @Operation(summary = "删除测试数据")
  @DeleteMapping("/{ids}")
  public R<?> remove(@PathVariable String ids) {
    List<Long> idList = Arrays.stream(ids.split(","))
      .map(Long::parseLong)
      .collect(Collectors.toList());
    return removeR(sysTestDataService.removeByIds(idList));
  }
}