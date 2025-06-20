package top.zhjh.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.bean.BeanUtil;
import top.csaf.coll.CollUtil;
import top.csaf.crypto.DigestUtil;
import top.csaf.lang.NumberUtil;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.base.model.PageVO;
import top.zhjh.config.tenant.TenantContext;
import top.zhjh.enums.RoleEnum;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysUserMapper;
import top.zhjh.model.entity.*;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysUserDetailVO;
import top.zhjh.model.vo.SysUserPageVO;
import top.zhjh.mybatis.MyServiceImpl;
import top.zhjh.mybatis.wrapper.MyLambdaQueryWrapper;
import top.zhjh.struct.SysUserStruct;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户 服务实现
 */
@Slf4j
@Service
public class SysUserService extends MyServiceImpl<SysUserMapper, SysUser> {

  @Resource
  private SysUserMapper sysUserMapper;
  @Resource
  private SysRoleUserService sysRoleUserService;
  @Resource
  private SysRoleService sysRoleService;
  @Resource
  private SysPostUserService sysPostUserService;
  @Resource
  private SysPostService sysPostService;
  @Resource
  private SysDeptService sysDeptService;
  @Resource
  private SysDeptUserService sysDeptUserService;
  @Resource
  private SysTenantService sysTenantService;
  @Resource
  private SysTenantUserService sysTenantUserService;
  @Resource
  private CacheManager cacheManager;

  /**
   * 列出用户角色编码
   *
   * @param id 用户 ID
   * @return 用户角色编码列表
   */
  @Cacheable(value = "userRoleCodes", key = "#id", condition = "#id != null && #id != ''")
  public List<String> listRoleCodes(Long id) {
    return sysUserMapper.listRoleCodes(id);
  }

  /**
   * 列出用户权限
   *
   * @param id 用户 ID
   * @return 用户权限列表
   */
  @Cacheable(value = "userPermissions", key = "#id", condition = "#id != null && #id != ''")
  public List<String> listPermission(Long id) {
    return sysUserMapper.listPermission(id);
  }

  /**
   * 登录
   *
   * @param username 用户名
   * @param password 密码
   * @param tenantId 租户 ID
   * @return 登录用户
   */
  public SysUserDetailVO login(@NonNull final String username, @NonNull final String password, final Long tenantId) {
    SysUser user = this.lambdaQuery()
      .select(SysUser::getRoleCodes, SysUser::getTenantIds, SysUser::getId, SysUser::getPassword)
      .eq(SysUser::getUsername, username).one();
    if (user == null) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "用户名或密码错误");
    }
    List<String> roleCodes = user.getRoleCodes();
    // 密码是否正确
    if (!user.getPassword().equals(DigestUtil.sha512Hex(password))
      // 非超管判断租户是否一致
      && (!StpExtUtil.isSuperAdmin(roleCodes) && !CollUtil.contains(user.getTenantIds(), tenantId))
    ) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "用户名或密码错误");
    }
    if (CollUtil.isEmpty(roleCodes)) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "用户未配置角色");
    }
    // 角色是否全部禁用
    if (sysRoleService.isAllDisabled(roleCodes)) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "用户角色已禁用");
    }
    SaLoginParameter saLoginParameter = new SaLoginParameter();
    // 扩展参数：租户 ID
    if (tenantId != null) {
      saLoginParameter.setExtra(BeanUtil.getPropertyName(BaseEntity::getTenantId), tenantId);
    }
    StpUtil.login(user.getId(), saLoginParameter);
    return this.getDetail(user.getId());
  }

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysUserPageVO> list(SysUserListQO query) {
    query.setTenantId(StpExtUtil.getTenantId());
    return sysUserMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public PageVO<SysUserPageVO> page(SysUserPageQO query) {
    query.setTenantId(StpExtUtil.getTenantId());
    List<SysUserPageVO> records = sysUserMapper.page(query);
    return new PageVO<SysUserPageVO>(query).setRecords(records);
  }

  /**
   * 获取详情
   *
   * @param id ID
   * @return 详情
   */
  public SysUserDetailVO getDetail(@NonNull final Long id) {
    SysUser sysUser = this.getById(id);
    if (sysUser == null) {
      throw new ServiceException("用户不存在");
    }
    SysUserDetailVO result = SysUserStruct.INSTANCE.toDetailVO(sysUser);
    // 是否显示租户
    result.setIsShowTenant(StpExtUtil.isSuperAdmin(sysUser.getRoleCodes()));
    return result;
  }

  /**
   * 关联角色
   *
   * @param sysUser 用户
   * @param roleIds 角色 ID 列表
   */
  private void assignRole(SysUser sysUser, List<Long> roleIds) {
    if (CollUtil.isEmpty(roleIds)) {
      log.warn("角色ID列表为空");
      return;
    }
    Long sysUserId = sysUser.getId();
    List<String> roleNames = new ArrayList<>();
    List<String> roleCodes = new ArrayList<>();
    List<SysRoleUser> roleUserList = new ArrayList<>();
    for (Long roleId : roleIds) {
      TenantContext.disable();
      SysRole role = sysRoleService.lambdaQuery()
        .select(SysRole::getName, SysRole::getCode)
        .eq(SysRole::getId, roleId).one();
      if (role == null) {
        log.error("角色不存在: {}", roleId);
        throw new ServiceException("角色不存在");
      }
      roleNames.add(role.getName());
      roleCodes.add(role.getCode());
      roleUserList.add(new SysRoleUser(roleId, sysUserId));
    }
    sysRoleUserService.saveBatch(roleUserList);
    sysUser.setRoleIds(roleIds);
    sysUser.setRoleNames(roleNames);
    sysUser.setRoleCodes(roleCodes);

    // 清除用户-角色编码缓存
    Objects.requireNonNull(cacheManager.getCache("userRoleCodes")).evict(sysUserId);
  }

  /**
   * 关联部门
   *
   * @param sysUser 用户
   * @param deptIds 部门 ID 列表
   */
  private void assignDept(SysUser sysUser, List<Long> deptIds) {
    if (CollUtil.isEmpty(deptIds)) {
      return;
    }
    Long sysUserId = sysUser.getId();
    List<String> deptNames = new ArrayList<>();
    List<SysDeptUser> deptUserList = new ArrayList<>();
    for (Long deptId : deptIds) {
      SysDept dept = sysDeptService.lambdaQuery()
        .select(SysDept::getName)
        .eq(SysDept::getId, deptId).one();
      if (dept == null) {
        log.error("部门不存在: {}", deptId);
        throw new ServiceException("部门不存在");
      }
      deptNames.add(dept.getName());
      deptUserList.add(new SysDeptUser(deptId, sysUserId));
    }
    sysDeptUserService.saveBatch(deptUserList);
    sysUser.setDeptIds(deptIds);
    sysUser.setDeptNames(deptNames);
  }

  /**
   * 关联岗位
   *
   * @param sysUser 用户
   * @param postIds 岗位 ID 列表
   */
  private void assignPost(SysUser sysUser, List<Long> postIds) {
    if (CollUtil.isEmpty(postIds)) {
      return;
    }
    Long sysUserId = sysUser.getId();
    List<String> postNames = new ArrayList<>();
    List<SysPostUser> postUserList = new ArrayList<>();
    for (Long postId : postIds) {
      SysPost post = sysPostService.lambdaQuery()
        .select(SysPost::getName)
        .eq(SysPost::getId, postId).one();
      if (post == null) {
        log.error("岗位不存在: {}", postId);
        throw new ServiceException("岗位不存在");
      }
      postNames.add(post.getName());
      postUserList.add(new SysPostUser(postId, sysUserId));
    }
    sysPostUserService.saveBatch(postUserList);
    sysUser.setPostIds(postIds);
    sysUser.setPostNames(postNames);
  }

  /**
   * 关联租户
   *
   * @param sysUser  用户
   * @param tenantId 租户 ID
   */
  private void assignTenant(SysUser sysUser, Long tenantId) {
    if (NumberUtil.leZero(tenantId)) {
      log.error("租户ID为空");
      return;
    }
    SysTenant tenant = sysTenantService.lambdaQuery()
      .select(SysTenant::getName)
      .eq(SysTenant::getId, tenantId).one();
    if (tenant == null) {
      log.error("租户不存在: {}", tenantId);
      throw new ServiceException("岗位不存在");
    }
    sysTenantUserService.save(new SysTenantUser(tenantId, sysUser.getId()));
    sysUser.setTenantIds(Collections.singletonList(tenantId));
    sysUser.setTenantNames(Collections.singletonList(tenant.getName()));
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysUserSaveQO obj) {
    // 用户名是否重复
    if (this.lambdaQuery().eq(SysUser::getUsername, obj.getUsername()).count() > 0) {
      throw new ServiceException("用户名已存在");
    }
    SysUser sysUser = SysUserStruct.INSTANCE.to(obj);
    sysUser.setPassword(DigestUtil.sha512Hex(sysUser.getPassword()));
    if (!this.save(sysUser)) {
      throw new ServiceException("保存失败");
    }
    Long sysUserId = sysUser.getId();

    SysUser updateObj = new SysUser();
    updateObj.setId(sysUserId);
    // 关联角色
    this.assignRole(updateObj, obj.getRoleIds());
    // 关联部门
    this.assignDept(updateObj, obj.getDeptIds());
    // 关联岗位
    this.assignPost(updateObj, obj.getPostIds());
    // 关联租户
    this.assignTenant(updateObj, StpExtUtil.getTenantId());

    return sysUserMapper.updateById(updateObj) > 0;
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysUserUpdateQO obj) {
    Long sysUserId = obj.getId();
    if (this.countById(sysUserId) == 0) {
      log.error("用户不存在: {}", sysUserId);
      throw new ServiceException("用户不存在");
    }

    // 删除旧关联
    sysRoleUserService.lambdaUpdate().eq(SysRoleUser::getUserId, sysUserId).remove();
    sysDeptUserService.lambdaUpdate().eq(SysDeptUser::getUserId, sysUserId).remove();
    sysPostUserService.lambdaUpdate().eq(SysPostUser::getUserId, sysUserId).remove();

    List<Long> roleIds = obj.getRoleIds();
    SysUser updateObj = SysUserStruct.INSTANCE.to(obj);
    // 关联角色
    this.assignRole(updateObj, roleIds);
    // 检查是否有超级管理员角色
    SysRole superAdminRole = sysRoleService.getSuperAdmin();
    List<SysRoleUser> roleUserList = sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, superAdminRole.getId()).list();
    if (roleUserList.size() == 1 && roleUserList.get(0).getUserId().equals(sysUserId) && !roleIds.contains(superAdminRole.getId())) {
      throw new ServiceException("至少保留一个编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的用户");
    }
    // 关联部门
    this.assignDept(updateObj, updateObj.getDeptIds());
    // 关联岗位
    this.assignPost(updateObj, updateObj.getPostIds());

    return sysUserMapper.updateById(updateObj) > 0;
  }

  /**
   * 删除
   *
   * @param query 删除入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysUserRemoveQO query) {
    List<Long> ids = query.getIds();
    if (CollUtil.isEmpty(ids)) {
      return false;
    }
    // SysRole superAdminRole = sysRoleService.getSuperAdmin();
    // List<SysRoleUser> roleUserList = sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, superAdminRole.getId()).list();

    List<Long> roleUserIds = new ArrayList<>();
    List<Long> deptUserIds = new ArrayList<>();
    List<Long> postUserIds = new ArrayList<>();
    for (Long id : ids) {
      SysUser user = this.getById(id);
      if (user == null) {
        log.error("用户不存在: {}", id);
        throw new ServiceException("用户不存在");
      }
      if (id.equals(StpUtil.getLoginIdAsLong())) {
        throw new ServiceException("不能删除自己");
      }
      // if (roleUserList.size() == 1 && roleUserList.getFirst().getUserId().equals(user.getId())) {
      //   throw new ServiceException("至少保留一个编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的用户");
      // }
      roleUserIds.add(id);
      deptUserIds.add(id);
      postUserIds.add(id);
    }
    // 删除关联
    sysRoleUserService.lambdaUpdate().in(SysRoleUser::getUserId, roleUserIds).remove();
    sysDeptUserService.lambdaUpdate().in(SysDeptUser::getUserId, deptUserIds).remove();
    sysPostUserService.lambdaUpdate().in(SysPostUser::getUserId, postUserIds).remove();
    // 删除用户
    return this.removeBatchByIds(ids);
  }

  /**
   * 修改密码
   *
   * @param obj 修改密码入参
   * @return 是否成功
   */
  public boolean updatePwd(SysUserUpdatePwdQO obj) {
    Long id = obj.getId();
    SysUser user = this.getById(id);
    if (user == null) {
      log.error("用户不存在: {}", id);
      throw new ServiceException("用户不存在");
    }
    if (!user.getPassword().equals(DigestUtil.sha512Hex(obj.getOldPassword()))) {
      throw new ServiceException("旧密码错误");
    }
    if (!obj.getNewPassword().equals(obj.getConfirmPassword())) {
      throw new ServiceException("新密码和确认密码不一致");
    }
    if (obj.getNewPassword().equals(obj.getOldPassword())) {
      throw new ServiceException("新密码和旧密码不能相同");
    }
    SysUser updateObj = new SysUser();
    updateObj.setId(id);
    updateObj.setPassword(DigestUtil.sha512Hex(obj.getNewPassword()));

    return this.updateById(updateObj);
  }

  /**
   * 获取超管用户 ID 列表
   *
   * @return 超管用户 ID 列表
   */
  public List<Long> getSuperAdminUserIds() {
    List<SysUser> sysUserList = this.list(new MyLambdaQueryWrapper<SysUser>()
      .jsonContains(SysUser::getRoleCodes, RoleEnum.SUPER_ADMIN.getCode())
      .select(SysUser::getId));
    if (CollUtil.isEmpty(sysUserList)) {
      return new ArrayList<>();
    }
    return sysUserList.stream().map(SysUser::getId).collect(Collectors.toList());
  }
}
