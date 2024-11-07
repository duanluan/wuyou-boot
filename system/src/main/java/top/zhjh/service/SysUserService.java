package top.zhjh.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.crypto.DigestUtil;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysUserMapper;
import top.zhjh.model.entity.SysUser;
import top.zhjh.model.qo.SysUserSaveQO;
import top.zhjh.struct.SysUserStruct;

import java.util.List;

/**
 * 用户 服务实现
 */
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

  @Autowired
  private SysUserMapper sysUserMapper;

  /**
   * 列出用户角色编码
   *
   * @param id 用户 ID
   * @return 用户角色编码列表
   */
  @Cacheable(value = "userRoleCodes", key = "#id", condition = "#id != null && #id != ''")
  public List<String> listRoleCodes(String id) {
    return sysUserMapper.listRoleCodes(id);
  }

  /**
   * 列出用户权限
   *
   * @param id 用户 ID
   * @return 用户权限列表
   */
  @Cacheable(value = "userPermissions", key = "#id", condition = "#id != null && #id != ''")
  public List<String> listPermission(String id) {
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
    SysUserSaveQO saveObj = new SysUserSaveQO();
    saveObj.setNickName(nickName);
    saveObj.setUsername(username);
    saveObj.setPassword(DigestUtil.sha512Hex(password));
    if (!save(saveObj)) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "注册失败");
    }
    Long id = saveObj.getId();
    SysUser updateObj = new SysUser();
    updateObj.setId(id);
    updateObj.setPassword(DigestUtil.sha512Hex(password));
    if (!this.updateById(updateObj)) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "注册失败");
    }
  }

  /**
   * 登录
   *
   * @param username 用户名
   * @param password 密码
   * @return 登录用户
   */
  public SysUser login(String username, String password) {
    SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    if (user == null) {
      throw new ServiceException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
    if (!user.getPassword().equals(DigestUtil.sha512Hex(password))) {
      throw new ServiceException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
    StpUtil.login(user.getId());
    return user;
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  public boolean save(SysUserSaveQO obj) {
    SysUser sysUser = SysUserStruct.INSTANCE.to(obj);
    boolean result = this.save(sysUser);
    obj.setId(sysUser.getId());
    return result;
  }
}
