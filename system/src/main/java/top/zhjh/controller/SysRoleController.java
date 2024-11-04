package top.zhjh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.csaf.lang.StrUtil;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.R;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysRoleGetVO;
import top.zhjh.service.SysRoleService;
import top.zhjh.struct.SysRoleStruct;

import java.util.List;

/**
 * 角色 控制器
 *
 * @author ZhongJianhao
 */
@Tag(name = "角色")
@Slf4j
@RequestMapping("/sys/roles")
@RestController
public class SysRoleController extends BaseController {

  @Autowired
  private SysRoleService sysRoleService;

  @Operation(summary = "角色列表")
  @GetMapping
  public R<List<SysRole>> list(@Validated SysRolePageQO query) {
    LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>()
      .like(StrUtil.isNotBlank(query.getName()), SysRole::getName, query.getName())
      .like(StrUtil.isNotBlank(query.getCode()), SysRole::getCode, query.getCode())
      .orderByDesc(SysRole::getId);
    if (query.getCurrent() == 0) {
      return ok(sysRoleService.list(queryWrapper));
    }
    return ok(sysRoleService.page(query, queryWrapper));
  }

  @Operation(summary = "角色详情")
  @GetMapping("/{id}")
  public R<SysRoleGetVO> get(@Validated SysRoleGetQO query) {
    return ok(sysRoleService.getById(query.getId()));
  }

  @Operation(summary = "保存角色")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PostMapping
  public R save(@RequestBody @Validated SysRoleSaveQO obj) {
    return saveR(sysRoleService.save(SysRoleStruct.INSTANCE.to(obj)));
  }

  @Operation(summary = "更新角色")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @PutMapping("/{id}")
  public R update(@RequestBody @Validated SysRoleUpdateQO obj) {
    return updateR(sysRoleService.updateById(SysRoleStruct.INSTANCE.to(obj)));
  }

  @Operation(summary = "删除角色")
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  @DeleteMapping("/{ids}")
  public R remove(@Validated SysRoleRemoveQO query) {
    return removeR(sysRoleService.remove(query));
  }
}
