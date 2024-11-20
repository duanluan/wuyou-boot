package top.zhjh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.csaf.tree.TreeNode;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysMenuGetVO;
import top.zhjh.service.SysMenuService;
import top.zhjh.struct.SysMenuStruct;

import javax.annotation.Resource;
import java.util.List;

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
  public R<List<TreeNode>> listTree(@Validated SysMenuTreeQO query) {
    return ok(sysMenuService.listTree(query));
  }

  @Operation(summary = "菜单详情")
  @GetMapping("/{id}")
  public R<SysMenuGetVO> get(@Validated SysMenuGetQO query) {
    return ok(sysMenuService.getById(query.getId()));
  }

  @Operation(summary = "保存菜单")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R save(@RequestBody @Validated SysMenuSaveQO obj) {
    return saveR(sysMenuService.save(SysMenuStruct.INSTANCE.to(obj)));
  }

  @Operation(summary = "更新菜单")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R update(@RequestBody @Validated SysMenuUpdateQO obj) {
    return updateR(sysMenuService.updateById(SysMenuStruct.INSTANCE.to(obj)));
  }

  @Operation(summary = "删除菜单")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R remove(@Validated SysMenuRemoveQO query) {
    return removeR(sysMenuService.removeByIds(query.getIds()));
  }
}
