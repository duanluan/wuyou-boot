package top.zhjh.service;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.bean.BeanUtil;
import top.csaf.coll.CollUtil;
import top.csaf.crypto.DigestUtil;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.base.model.PageVO;
import top.zhjh.enums.RoleEnum;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysUserMapper;
import top.zhjh.model.entity.*;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysUserDetailVO;
import top.zhjh.model.vo.SysUserPageVO;
import top.zhjh.mybatis.MyServiceImpl;
import top.zhjh.struct.SysUserStruct;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
  @Autowired
  private SysRoleService sysRoleService;
  @Resource
  private SysPostUserService sysPostUserService;
  @Resource
  private SysPostService sysPostService;
  @Resource
  private SysDeptService sysDeptService;
  @Resource
  private SysDeptUserService sysDeptUserService;

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
   * 注册
   *
   * @param nickName 昵称
   * @param username 用户名
   * @param password 密码
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public void register(String nickName, String username, String password) {
    SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getNickName, username));
    if (user != null) {
      throw new ServiceException(HttpStatus.CONFLICT, "昵称已存在");
    }
    user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    if (user != null) {
      throw new ServiceException(HttpStatus.CONFLICT, "用户名已存在");
    }
    SysUser user1 = new SysUser();
    user1.setNickName(nickName);
    user1.setUsername(username);
    user1.setPassword(DigestUtil.sha512Hex(password));
    if (!this.save(user1)) {
      throw new ServiceException("注册失败");
    }
    Long id = user1.getId();
    SysUser updateObj = new SysUser();
    updateObj.setId(id);
    updateObj.setPassword(DigestUtil.sha512Hex(password));
    if (!this.updateById(updateObj)) {
      throw new ServiceException("注册失败");
    }
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
    SysUser user = this.lambdaQuery().select(SysUser::getId, SysUser::getPassword).eq(SysUser::getUsername, username).one();
    if (user == null) {
      throw new ServiceException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
    if (!user.getPassword().equals(DigestUtil.sha512Hex(password))) {
      throw new ServiceException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
    StpUtil.login(user.getId(),
      // 扩展参数：租户 ID
      SaLoginConfig.setExtra(BeanUtil.getPropertyName(BaseEntity::getTenantId), tenantId));
    return this.getDetail(user.getId());
  }

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysUserPageVO> list(SysUserListQO query) {
    return sysUserMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public PageVO<SysUserPageVO> page(SysUserPageQO query) {
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
    return sysUserMapper.getDetail(id);
  }

  /**
   * 关联角色
   *
   * @param sysUser 用户
   * @param roleIds 角色 ID 列表
   */
  private void assignRole(SysUser sysUser, List<Long> roleIds) {
    if (CollUtil.isEmpty(roleIds)) {
      return;
    }
    Long sysUserId = sysUser.getId();
    List<String> roleNames = new ArrayList<>();
    List<SysRoleUser> roleUserList = new ArrayList<>();
    for (Long roleId : roleIds) {
      SysRole role = sysRoleService.lambdaQuery()
        .select(SysRole::getName)
        .eq(SysRole::getId, roleId).one();
      if (role == null) {
        log.error("角色不存在: {}", roleId);
        throw new ServiceException("角色不存在");
      }
      roleNames.add(role.getName());
      roleUserList.add(new SysRoleUser(roleId, sysUserId));
    }
    sysRoleUserService.saveBatch(roleUserList);
    sysUser.setRoleIds(roleIds);
    sysUser.setRoleNames(roleNames);
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

    return this.updateById(updateObj);
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

    // 删除旧关联
    sysRoleUserService.lambdaUpdate().eq(SysRoleUser::getUserId, sysUserId).remove();
    sysDeptUserService.lambdaUpdate().eq(SysDeptUser::getUserId, sysUserId).remove();
    sysPostUserService.lambdaUpdate().eq(SysPostUser::getUserId, sysUserId).remove();

    return this.updateById(updateObj);
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
    SysRole superAdminRole = sysRoleService.getSuperAdmin();
    List<SysRoleUser> roleUserList = sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, superAdminRole.getId()).list();

    List<Long> roleUserIds = new ArrayList<>();
    List<Long> deptUserIds = new ArrayList<>();
    List<Long> postUserIds = new ArrayList<>();
    for (Long id : ids) {
      SysUser user = this.getById(id);
      if (user == null) {
        log.error("用户不存在: {}", id);
        throw new ServiceException("用户不存在");
      }
      if (roleUserList.size() == 1 && roleUserList.get(0).getUserId().equals(user.getId())) {
        throw new ServiceException("至少保留一个编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的用户");
      }
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
}
