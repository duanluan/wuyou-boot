package top.zhjh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.csaf.tree.TreeNode;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.*;
import top.zhjh.service.SysMenuService;
import top.zhjh.struct.SysMenuStruct;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 菜单 控制器
 */
@Tag(name = "菜单")
@Slf4j
@RequestMapping("/sys/menus")
@RestController
public class SysMenuController extends BaseController {

  @Resource
  private SysMenuService sysMenuService;
  @Resource
  private CacheManager cacheManager;

  @Operation(summary = "菜单列表")
  @GetMapping
  public R<List<SysMenu>> list(@Validated SysMenuPageQO query) {
    LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<SysMenu>().orderByDesc(SysMenu::getId);
    if (query.getCurrent() == 0) {
      return ok(sysMenuService.list(queryWrapper));
    }
    return ok(sysMenuService.page(query, queryWrapper));
  }

  @Operation(summary = "菜单树")
  @GetMapping("/tree")
  public R<List<TreeNode>> listTree() {
    return ok(sysMenuService.listTree());
  }

  @Operation(summary = "刷新缓存")
  @PostMapping("/refreshTreeCache")
  public R<?> refreshTreeCache() {
    // 清除 @Cacheable 缓存
    Objects.requireNonNull(cacheManager.getCache("menuTree")).clear();
    return ok();
  }

  @Operation(summary = "菜单树表格")
  @GetMapping("/treeTable")
  public R<List<TreeNode>> listTreeTable(@Validated SysMenuTreeTableQO query) {
    return ok(sysMenuService.listTreeTable(query));
  }

  @Operation(summary = "菜单详情")
  @GetMapping("/{id}")
  public R<SysMenu> get(@Validated SysMenuGetQO query) {
    return ok(sysMenuService.getById(query.getId()));
  }

  @CacheEvict(value = "menuTree", keyGenerator = "roleCodesCacheKeyGen")
  @Operation(summary = "保存菜单")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R save(@RequestBody @Validated SysMenuSaveQO obj) {
    return saveR(sysMenuService.save(obj));
  }

  @CacheEvict(value = "menuTree", keyGenerator = "roleCodesCacheKeyGen")
  @Operation(summary = "更新菜单")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R update(@RequestBody @Validated(SysMenuUpdateQO.UpdateGroup.class) SysMenuUpdateQO obj) {
    return updateR(sysMenuService.updateById(SysMenuStruct.INSTANCE.to(obj)));
  }

  @Operation(summary = "更新菜单状态")
  @PatchMapping("/{id}/status")
  public R<?> updateStatus(@RequestBody @Validated(SysMenuUpdateQO.UpdateStatusGroup.class) SysMenuUpdateQO obj) {
    return updateR(sysMenuService.updateStatus(obj));
  }

  @CacheEvict(value = "menuTree", keyGenerator = "roleCodesCacheKeyGen")
  @Operation(summary = "删除菜单")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R remove(@Validated SysMenuRemoveQO query) {
    return removeR(sysMenuService.removeByIds(query.getIds()));
  }
}
