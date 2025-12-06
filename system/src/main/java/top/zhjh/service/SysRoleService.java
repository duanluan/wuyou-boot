package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.coll.CollUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.base.model.PageVO;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.DataScopeActionType;
import top.zhjh.enums.DataScopeType;
import top.zhjh.enums.RoleEnum;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysRoleMapper;
import top.zhjh.model.entity.*;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysRolePageVO;
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
  @Autowired
  private SysTenantService sysTenantService;

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysRolePageVO> list(SysRoleListQO query) {
    return sysRoleMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public PageVO<SysRolePageVO> page(SysRolePageQO query) {
    List<SysRolePageVO> records = sysRoleMapper.page(query);
    return new PageVO<SysRolePageVO>(query).setRecords(records);
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysRoleUpdateQO obj) {
    SysRole existRole = this.lambdaQuery()
      .select(SysRole::getCode, SysRole::getStatus)
      .eq(SysRole::getId, obj.getId()).one();
    if (existRole == null) {
      log.error("角色不存在: {}", obj.getId());
      throw new ServiceException("参数错误");
    }
    if (StpExtUtil.isSuperAdmin(existRole)) {
      if (!existRole.getCode().equals(obj.getCode())) {
        throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的编码");
      }
      if (!existRole.getStatus().equals(obj.getStatus())) {
        throw new ServiceException("不能修改编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的状态");
      }
    }

    this.lambdaUpdate().eq(SysRole::getId, obj.getId())
      .func(wrapper -> {
        Long tenantId = obj.getTenantId();
        if (tenantId == null) {
          wrapper.set(SysRole::getTenantId, null);
          wrapper.set(SysRole::getTenantName, null);
        }else{
          SysTenant tenant = sysTenantService.lambdaQuery()
            .select(SysTenant::getName)
            .eq(SysTenant::getId, tenantId).one();
          if (tenant == null) {
            log.error("租户不存在: {}", tenantId);
            throw new ServiceException("参数错误");
          }
          wrapper.set(SysRole::getTenantId, tenantId);
          wrapper.set(SysRole::getTenantName, tenant.getName());
        }
      })
      .set(StrUtil.isNotBlank(obj.getCode()), SysRole::getCode, obj.getCode())
      .set(StrUtil.isNotBlank(obj.getName()), SysRole::getName, obj.getName())
      .set(obj.getStatus() != null, SysRole::getStatus, obj.getStatus())
      .set(obj.getSort() != null, SysRole::getSort, obj.getSort())
      .set(SysRole::getDescription, obj.getDescription())
      .update();
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
    List<SysRole> existRoleList = this.lambdaQuery()
      .select(SysRole::getName, SysRole::getCode)
      .and(c -> c.eq(SysRole::getName, obj.getName()).or().eq(SysRole::getCode, obj.getCode()))
      .list();
    if (CollUtil.isNotEmpty(existRoleList)) {
      List<String> errorMsgFields = new ArrayList<>();
      for (SysRole existRole : existRoleList) {
        if (existRole.getName().equals(obj.getName())) {
          errorMsgFields.add("名称");
        }
        if (existRole.getCode().equals(obj.getCode())) {
          errorMsgFields.add("编码");
        }
      }
      throw new ServiceException(StrUtil.join(errorMsgFields, "、") + "已存在");
    }

    SysRole sysRole = SysRoleStruct.INSTANCE.to(obj);
    Long tenantId = obj.getTenantId();
    if (tenantId != null) {
      SysTenant tenant = sysTenantService.lambdaQuery()
        .select(SysTenant::getName)
        .eq(SysTenant::getId, tenantId).one();
      if (tenant == null) {
        log.error("租户不存在: {}", tenantId);
        throw new ServiceException("参数错误");
      }
      sysRole.setTenantName(tenant.getName());
    }

    return this.save(sysRole);
  }

  /**
   * 获取超级管理员角色
   *
   * @return 超级管理员角色
   */
  public SysRole getSuperAdmin() {
    TenantContext.disable();
    return this.lambdaQuery().eq(SysRole::getCode, RoleEnum.SUPER_ADMIN.getCode()).one();
  }

  /**
   * 角色是否全部已禁用
   *
   * @param roleCodes 角色编码列表
   * @return {@code true} 角色编码列表中的角色全部已禁用
   */
  public boolean isAllDisabled(List<String> roleCodes) {
    List<SysRole> sysRoleList = this.lambdaQuery()
      .select(SysRole::getStatus)
      .in(SysRole::getCode, roleCodes).list();
    for (SysRole sysRole : sysRoleList) {
      if (!CommonStatus.DISABLE.equals(sysRole.getStatus())) {
        return false;
      }
    }
    return true;
  }
}
