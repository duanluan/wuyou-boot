package top.zhjh.service;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import top.zhjh.model.vo.SysUserPageVO;
import top.zhjh.struct.SysUserStruct;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户 服务实现
 */
@Slf4j
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

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
   * @param tenantId
   * @return 登录用户
   */
  public SysUser login(String username, String password, Long tenantId) {
    SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    if (user == null) {
      throw new ServiceException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
    if (!user.getPassword().equals(DigestUtil.sha512Hex(password))) {
      throw new ServiceException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
    StpUtil.login(user.getId(),
      // 扩展参数
      SaLoginConfig.setExtra(BeanUtil.getPropertyName(BaseEntity::getTenantId), tenantId));
    return user;
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

    // 关联角色
    for (Long roleId : obj.getRoleIds()) {
      SysRole role = sysRoleService.getById(roleId);
      if (role == null) {
        log.error("角色不存在: {}", roleId);
        throw new ServiceException("角色不存在");
      }
      sysRoleUserService.save(new SysRoleUser(roleId, sysUserId));
    }

    // 关联岗位
    for (Long postId : obj.getPostIds()) {
      SysPost sysPost = sysPostService.getById(postId);
      if (sysPost == null) {
        log.error("岗位不存在: {}", postId);
        throw new ServiceException("岗位不存在");
      }
      sysPostUserService.save(new SysPostUser(postId, sysUserId));
    }

    return true;
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysUserUpdateQO obj) {
    Long id = obj.getId();
    SysUser user = this.getById(id);
    if (user == null) {
      log.error("用户不存在: {}", id);
      throw new ServiceException("用户不存在");
    }
    List<Long> roleIds = obj.getRoleIds();
    for (Long roleId : roleIds) {
      SysRole role = sysRoleService.getById(roleId);
      if (role == null) {
        log.error("角色不存在: {}", roleId);
        throw new ServiceException("角色不存在");
      }
    }
    SysRole superAdminRole = sysRoleService.lambdaQuery().eq(SysRole::getCode, RoleEnum.SUPER_ADMIN.getCode()).one();
    List<SysRoleUser> roleUserList = sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, superAdminRole.getId()).list();
    if (roleUserList.size() == 1 && roleUserList.get(0).getUserId().equals(user.getId()) && !roleIds.contains(superAdminRole.getId())) {
      throw new ServiceException("至少保留一个编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的用户");
    }
    // 重新关联角色
    sysRoleUserService.lambdaUpdate().eq(SysRoleUser::getUserId, id).remove();
    for (Long roleId : roleIds) {
      sysRoleUserService.save(new SysRoleUser(roleId, id));
    }

    // 重新关联岗位
    sysPostUserService.lambdaUpdate().eq(SysPostUser::getUserId, id).remove();
    for (Long postId : obj.getPostIds()) {
      sysPostUserService.save(new SysPostUser(postId, id));
    }

    return this.updateById(SysUserStruct.INSTANCE.to(obj));
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
    SysRole superAdminRole = sysRoleService.lambdaQuery().eq(SysRole::getCode, RoleEnum.SUPER_ADMIN.getCode()).one();
    List<SysRoleUser> roleUserList = sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, superAdminRole.getId()).list();

    for (Long id : ids) {
      SysUser user = this.getById(id);
      if (user == null) {
        log.error("用户不存在: {}", id);
        throw new ServiceException("用户不存在");
      }
      if (roleUserList.size() == 1 && roleUserList.get(0).getUserId().equals(user.getId())) {
        throw new ServiceException("至少保留一个编码为" + RoleEnum.SUPER_ADMIN.getCode() + "角色的用户");
      }
      // 删除关联角色
      sysRoleUserService.lambdaUpdate().eq(SysRoleUser::getUserId, id).remove();
    }
    return this.removeBatchByIds(ids);
  }
}
