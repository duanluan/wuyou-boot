package top.zhjh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.csaf.tree.TreeNode;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.qo.*;
import top.zhjh.service.SysDeptService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门 控制器
 */
@Tag(name = "部门")
@Slf4j
@RequestMapping(value = "/sys/depts", consumes = MediaType.ALL_VALUE)
@RestController
public class SysDeptController extends BaseController {

  @Resource
  private SysDeptService sysDeptService;

  @Operation(summary = "部门列表")
  @GetMapping
  public R<?> list(@Validated SysDeptPageQO query) {
    if (query.getCurrent() == 0) {
      return ok(sysDeptService.list(query));
    }
    return ok(sysDeptService.page(query));
  }

  @Operation(summary = "部门树")
  @GetMapping("/tree")
  public R<List<TreeNode>> listTree(@Validated SysDeptTreeQO query) {
    return ok(sysDeptService.listTree(query));
  }

  @Operation(summary = "保存部门")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R<?> save(@RequestBody @Validated SysDeptSaveQO obj) {
    return saveR(sysDeptService.save(obj));
  }

  @Operation(summary = "更新部门")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R<?> update(@RequestBody @Validated(SysDeptUpdateQO.UpdateGroup.class) SysDeptUpdateQO obj) {
    return updateR(sysDeptService.update(obj));
  }

  @Operation(summary = "更新部门状态")
  @PatchMapping("/{id}/status")
  public R<?> updateStatus(@RequestBody @Validated(SysDeptUpdateQO.UpdateStatusGroup.class) SysDeptUpdateQO obj) {
    return updateR(sysDeptService.updateStatus(obj));
  }

  @Operation(summary = "删除部门")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R<?> remove(@Validated SysDeptRemoveQO query) {
    return removeR(sysDeptService.remove(query));
  }
}
