package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.coll.CollUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.DataScopeActionType;
import top.zhjh.enums.DataScopeType;
import top.zhjh.enums.RoleEnum;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysRoleMapper;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.entity.SysRoleDept;
import top.zhjh.model.entity.SysRoleMenu;
import top.zhjh.model.entity.SysRoleUser;
import top.zhjh.model.qo.*;
import top.zhjh.struct.SysRoleStruct;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色 服务实现
 */
@Slf4j
@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

  @Resource
  private SysRoleMapper sysRoleMapper;
  @Resource
  private SysRoleUserService sysRoleUserService;
  @Resource
  private SysRoleMenuService sysRoleMenuService;
  @Resource
  private SysRoleDeptService sysRoleDeptService;

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysRoleUpdateQO obj) {
    SysRole role = this.getById(obj.getId());
    if (role == null) {
      log.error("角色不存在: {}", obj.getId());
      throw new ServiceException("角色不存在");
    }
    if (StpExtUtil.isSuperAdmin(role)) {
      if (!role.getCode().equals(obj.getCode())) {
        throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的编码");
      }
      if (!role.getStatus().equals(obj.getStatus())) {
        throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的状态");
      }
    }
    this.updateById(SysRoleStruct.INSTANCE.to(obj));
    return true;
  }

  /**
   * 更新状态
   *
   * @param obj 更新状态入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean updateStatus(SysRoleUpdateStatusQO obj) {
    if (CommonStatus.isInEnum(obj.getStatus())) {
      throw new ServiceException("角色状态错误");
    }
    SysRole role = this.getById(obj.getId());
    if (role == null) {
      log.error("角色不存在: {}", obj.getId());
      throw new ServiceException("角色不存在");
    }
    if (StpExtUtil.isSuperAdmin(role)) {
      throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的状态");
    }
    return this.updateById(SysRoleStruct.INSTANCE.to(obj));
  }

  /**
   * 更新角色菜单权限
   *
   * @param obj 更新角色菜单权限入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean updateMenus(SysRoleUpdateMenusQO obj) {
    Long id = obj.getId();
    SysRole role = this.getById(id);
    if (role == null) {
      log.error("角色不存在: {}", id);
      throw new ServiceException("角色不存在");
    }
    if (StpExtUtil.isSuperAdmin(role)) {
      throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的菜单权限");
    }
    sysRoleMenuService.lambdaUpdate().eq(SysRoleMenu::getRoleId, obj.getId()).remove();
    if (CollUtil.isNotEmpty(obj.getMenuIds())) {
      List<SysRoleMenu> roleMenuList = new ArrayList<>();
      for (Long menuId : obj.getMenuIds()) {
        roleMenuList.add(new SysRoleMenu(id, menuId));
      }
      if (!sysRoleMenuService.saveBatch(roleMenuList)) {
        throw new ServiceException("保存角色菜单权限失败");
      }
    }
    return true;
  }

  /**
   * 更新角色数据权限
   *
   * @param obj 更新角色数据权限入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean updateDataScope(SysRoleUpdateDataScopeQO obj) {
    Long id = obj.getId();
    SysRole role = this.getById(id);
    if (role == null) {
      log.error("角色不存在: {}", id);
      throw new ServiceException("角色不存在");
    }
    if (StpExtUtil.isSuperAdmin(role)) {
      throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的数据权限");
    }
    sysRoleDeptService.lambdaUpdate().eq(SysRoleDept::getRoleId, obj.getId()).remove();
    if (DataScopeType.CUSTOM.equals(obj.getQueryDataScope()) && CollUtil.isNotEmpty(obj.getQueryDataScopeDeptIds())) {
      List<SysRoleDept> roleDeptList = new ArrayList<>();
      for (Long deptId : obj.getQueryDataScopeDeptIds()) {
        roleDeptList.add(new SysRoleDept(id, deptId, DataScopeActionType.QUERY));
      }
      if (!sysRoleDeptService.saveBatch(roleDeptList)) {
        throw new ServiceException("保存角色查询数据权限失败");
      }
    }
    if (DataScopeType.CUSTOM.equals(obj.getUpdateDataScope()) && CollUtil.isNotEmpty(obj.getUpdateDataScopeDeptIds())) {
      List<SysRoleDept> roleDeptList = new ArrayList<>();
      for (Long deptId : obj.getUpdateDataScopeDeptIds()) {
        roleDeptList.add(new SysRoleDept(id, deptId, DataScopeActionType.UPDATE));
      }
      if (!sysRoleDeptService.saveBatch(roleDeptList)) {
        throw new ServiceException("保存角色增删改数据权限失败");
      }
    }
    this.updateById(SysRoleStruct.INSTANCE.to(obj));
    return true;
  }

  /**
   * 删除
   *
   * @param query 删除入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysRoleRemoveQO query) {
    List<Long> ids = query.getIds();
    if (CollUtil.isEmpty(ids)) {
      return false;
    }
    for (Long id : ids) {
      SysRole role = this.getById(id);
      if (role == null) {
        log.error("角色不存在: {}", id);
        throw new ServiceException("角色不存在");
      }
      if (StpExtUtil.isSuperAdmin(role)) {
        throw new ServiceException("不能删除编码为" + RoleEnum.SUPER_ADMIN.getCode() + "的角色");
      }
      if (sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, id).count() > 0) {
        throw new ServiceException("不能删除关联了用户的角色");
      }
      // 删除关联菜单
      sysRoleMenuService.lambdaUpdate().eq(SysRoleMenu::getRoleId, id).remove();
    }
    return this.removeBatchByIds(ids);
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysRoleSaveQO obj) {
    // 名称或编码是否重复
    List<SysRole> roleList = this.lambdaQuery().select(SysRole::getName, SysRole::getCode)
      .and(c -> c.eq(SysRole::getName, obj.getName()).or().eq(SysRole::getCode, obj.getCode()))
      .list();
    if (CollUtil.isNotEmpty(roleList)) {
      List<String> errorMsgFields = new ArrayList<>();
      for (SysRole role : roleList) {
        if (role.getName().equals(obj.getName())) {
          errorMsgFields.add("名称");
        }
        if (role.getCode().equals(obj.getCode())) {
          errorMsgFields.add("编码");
        }
      }
      throw new ServiceException(StrUtil.join(errorMsgFields, "、") + "已存在");
    }
    return this.save(SysRoleStruct.INSTANCE.to(obj));
  }
}
