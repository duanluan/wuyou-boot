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
import top.zhjh.util.PasswordCipher;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户服务实现。
 * <p>
 * 负责用户登录、查询、保存、更新、删除与密码修改；
 * 维护用户与角色/部门/岗位/租户的关联数据；
 * 结合权限缓存、租户上下文以及数据权限相关逻辑提供统一入口。
 * </p>
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
  @Resource
  private PasswordCipher passwordCipher;

  /**
   * 获取用户角色编码列表。
   * <p>
   * 结果会缓存到 {@code userRoleCodes}，用于鉴权与权限校验。
   * </p>
   *
   * @param id 用户 ID
   * @return 角色编码列表
   */
  @Cacheable(value = "userRoleCodes", key = "#id", condition = "#id != null && #id != ''")
  public List<String> listRoleCodes(Long id) {
    return sysUserMapper.listRoleCodes(id);
  }

  /**
   * 获取用户权限列表。
   * <p>
   * 结果会缓存到 {@code userPermissions}，用于鉴权与权限校验。
   * </p>
   *
   * @param id 用户 ID
   * @return 权限列表
   */
  @Cacheable(value = "userPermissions", key = "#id", condition = "#id != null && #id != ''")
  public List<String> listPermission(Long id) {
    return sysUserMapper.listPermission(id);
  }

  /**
   * 登录。
   * <p>
   * 校验流程：
   * <ol>
   *   <li>校验用户名与密码是否正确。</li>
   *   <li>非超管校验租户是否匹配。</li>
   *   <li>校验角色是否存在且未全部禁用。</li>
   *   <li>写入租户扩展参数并完成登录。</li>
   * </ol>
   * 若检测到旧的 SHA-512 密码，会在登录成功后自动升级为新密文格式。
   * </p>
   *
   * @param username 用户名
   * @param password 密码
   * @param tenantId 租户 ID
   * @return 登录用户详情
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
    if (!passwordCipher.matches(password, user.getPassword())) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "用户名或密码错误");
    }
    // 非超管判断租户是否一致
    if (!StpExtUtil.isSuperAdmin(roleCodes) && !CollUtil.contains(user.getTenantIds(), tenantId)) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "用户名或密码错误");
    }
    // 登录成功后自动升级旧的 SHA-512 密码。
    if (passwordCipher.shouldUpgradeLegacy(user.getPassword())) {
      SysUser updateObj = new SysUser();
      updateObj.setId(user.getId());
      updateObj.setPassword(passwordCipher.encode(password));
      this.updateById(updateObj);
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
   * 列出用户列表。
   * <p>
   * 自动注入当前登录租户，用于多租户隔离。
   * </p>
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysUserPageVO> list(SysUserListQO query) {
    query.setTenantId(StpExtUtil.getTenantId());
    return sysUserMapper.list(query);
  }

  /**
   * 分页查询用户列表。
   * <p>
   * 自动注入当前登录租户，用于多租户隔离。
   * </p>
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
   * 获取用户详情。
   * <p>
   * 若不存在则抛出异常；同时根据角色判断是否展示租户信息。
   * </p>
   *
   * @param id 用户 ID
   * @return 用户详情
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
   * 关联角色并回填用户的角色信息字段。
   * <p>
   * 过程包括：读取角色信息、保存用户-角色关联、回写 roleIds/roleNames/roleCodes，
   * 并清理对应的角色编码缓存。
   * </p>
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
      SysRole role = TenantContext.supplyWithoutTenant(() -> sysRoleService.lambdaQuery()
        .select(SysRole::getName, SysRole::getCode)
        .eq(SysRole::getId, roleId).one());
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
   * 关联部门并回填用户的部门信息字段。
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
   * 关联岗位并回填用户的岗位信息字段。
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
   * 关联租户并回填用户的租户信息字段。
   * <p>
   * 当未传入租户列表时，默认绑定当前登录租户。
   * </p>
   *
   * @param sysUser   用户
   * @param tenantIds 租户 ID 列表
   */
  private void assignTenant(SysUser sysUser, List<Long> tenantIds) {
    if (CollUtil.isEmpty(tenantIds)) {
      Long currentTenantId = StpExtUtil.getTenantId();
      if (currentTenantId == null) {
        return;
      } else {
        tenantIds = new ArrayList<>();
        tenantIds.add(currentTenantId);
      }
    }
    Long sysUserId = sysUser.getId();
    List<String> tenantNames = new ArrayList<>();
    List<SysTenantUser> tenantUserList = new ArrayList<>();
    for (Long tenantId : tenantIds) {
      SysTenant tenant = sysTenantService.lambdaQuery()
        .select(SysTenant::getName)
        .eq(SysTenant::getId, tenantId).one();
      if (tenant == null) {
        log.error("租户不存在: {}", tenantId);
        throw new ServiceException("租户不存在");
      }
      tenantNames.add(tenant.getName());
      tenantUserList.add(new SysTenantUser(tenantId, sysUserId));
    }
    sysTenantUserService.saveBatch(tenantUserList);
    sysUser.setTenantIds(tenantIds);
    sysUser.setTenantNames(tenantNames);
  }

  /**
   * 保存用户。
   * <p>
   * 处理流程：
   * <ol>
   *   <li>校验用户名唯一性。</li>
   *   <li>保存用户基本信息（含加密密码）。</li>
   *   <li>关联角色、部门、岗位、租户。</li>
   *   <li>回写扩展字段并更新用户。</li>
   * </ol>
   * 整个过程受事务保护，保证一致性。
   * </p>
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
    sysUser.setPassword(passwordCipher.encode(sysUser.getPassword()));
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
    this.assignTenant(updateObj, obj.getTenantIds());

    return sysUserMapper.updateById(updateObj) > 0;
  }

  /**
   * 更新用户。
   * <p>
   * 处理流程：
   * <ol>
   *   <li>校验用户是否存在。</li>
   *   <li>禁止当前用户修改自身角色。</li>
   *   <li>删除旧的角色/部门/岗位/租户关联。</li>
   *   <li>重新绑定关联并回写扩展字段。</li>
   *   <li>校验至少保留一个超级管理员。</li>
   * </ol>
   * 整个过程受事务保护，保证一致性。
   * </p>
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
    if (sysUserId.equals(StpUtil.getLoginIdAsLong())) {
      if (obj.getRoleIds() != null) {
        throw new ServiceException("不能修改自己的角色");
      }
    }

    // 删除旧关联
    sysRoleUserService.lambdaUpdate().eq(SysRoleUser::getUserId, sysUserId).remove();
    sysDeptUserService.lambdaUpdate().eq(SysDeptUser::getUserId, sysUserId).remove();
    sysPostUserService.lambdaUpdate().eq(SysPostUser::getUserId, sysUserId).remove();
    sysTenantUserService.lambdaUpdate().eq(SysTenantUser::getUserId, sysUserId).remove();

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
    // 关联租户
    this.assignTenant(updateObj, updateObj.getTenantIds());

    return sysUserMapper.updateById(updateObj) > 0;
  }

  /**
   * 删除用户。
   * <p>
   * 处理流程：
   * <ol>
   *   <li>校验用户是否存在并禁止删除自己。</li>
   *   <li>删除用户与角色/部门/岗位的关联。</li>
   *   <li>批量删除用户记录。</li>
   * </ol>
   * </p>
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
   * 修改密码。
   * <p>
   * 校验旧密码、新密码与确认密码的一致性，
   * 并使用 {@link PasswordCipher} 进行加密后更新。
   * </p>
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
    if (!passwordCipher.matches(obj.getOldPassword(), user.getPassword())) {
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
    updateObj.setPassword(passwordCipher.encode(obj.getNewPassword()));

    return this.updateById(updateObj);
  }

  /**
   * 获取超管用户 ID 列表。
   * <p>
   * 通过 JSON 字段中的角色编码过滤，返回符合条件的用户 ID。
   * </p>
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
