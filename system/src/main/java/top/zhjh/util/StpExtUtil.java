package top.zhjh.util;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.csaf.bean.BeanUtil;
import top.csaf.coll.CollUtil;
import top.csaf.lang.NumberUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.enums.RoleEnum;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.entity.SysUser;
import top.zhjh.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Sa-Token StpUtil 扩展
 */
@Slf4j
@Component
public class StpExtUtil {

  private static SysUserService sysUserService;

  @Resource
  private void setSysUserService(SysUserService sysUserService) {
    StpExtUtil.sysUserService = sysUserService;
  }

  /**
   * 是否为超级管理员
   *
   * @param sysRole 角色
   * @return 是否为超级管理员
   */
  public static boolean isSuperAdmin(SysRole sysRole) {
    if (sysRole == null || StrUtil.isBlank(sysRole.getCode())) {
      throw new IllegalArgumentException("sysRole is null or code is blank");
    }
    return RoleEnum.SUPER_ADMIN.getCode().equals(sysRole.getCode());
  }

  /**
   * 当前用户是否为超级管理员
   *
   * @return 当前用户是否为超级管理员
   */
  public static boolean isSuperAdmin() {
    List<String> roleList = StpUtil.getRoleList();
    if (CollUtil.isEmpty(roleList)) {
      return false;
    }
    for (String roleCode : roleList) {
      if (RoleEnum.SUPER_ADMIN.getCode().equals(roleCode)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 角色编码列表中是否有超级管理员
   *
   * @param roleCodes 角色编码列表
   * @return {code true} 角色编码列表中有超级管理员
   */
  public static boolean isSuperAdmin(List<String> roleCodes) {
    return CollUtil.contains(roleCodes, RoleEnum.SUPER_ADMIN.getCode());
  }

  /**
   * 用户是否为超级管理员
   *
   * @param sysUserId 用户 ID
   * @return {code true} 用户为超级管理员
   */
  public static boolean isSuperAdmin(Long sysUserId) {
    SysUser sysUser = sysUserService.lambdaQuery()
      .select(SysUser::getRoleCodes)
      .eq(SysUser::getId, sysUserId).one();
    if (sysUser == null) {
      return false;
    }
    return isSuperAdmin(sysUser.getRoleCodes());
  }

  // /**
  //  * Sa-Token JWT 是否已初始化
  //  */
  // public static final AtomicBoolean INITIALIZED_JWT = new AtomicBoolean(false);
  //
  // @PostConstruct
  // public void initJwt() {
  //   INITIALIZED_JWT.set(true);
  //   log.info("sa-token-jwt插件已加载完成");
  // }

  /**
   * 获取当前登录用户的租户 ID
   *
   * @return 当前登录用户的租户 ID
   */
  public static Long getTenantId() {
    // // JWT 未初始化返回 null
    // if (!INITIALIZED_JWT.get()) {
    //   return null;
    // }
    return NumberUtil.createLong(StpUtil.getExtra(BeanUtil.getPropertyName(BaseEntity::getTenantId)));
  }

  private static final String IS_ENABLE_TENANT_KEY = "isEnableTenant";

  /**
   * 当前线程是否启用租户
   *
   * @return 是否启用租户
   */
  public static boolean isEnableTenant() {
    // Object value = ThreadLocalUtil.get(IS_ENABLE_TENANT_KEY);
    // if (value == null) {
    //   return true;
    // }
    // return (boolean) value;
    return InterceptorIgnoreHelper.hasIgnoreStrategy();
  }

  /**
   * 当前线程禁用租户
   */
  public static void disableTenant() {
    // if (!isEnableTenant()) {
    //   return;
    // }
    // log.debug("禁用租户过滤，线程ID: {}", Thread.currentThread().getId(), new Exception("调用栈"));
    // ThreadLocalUtil.set(IS_ENABLE_TENANT_KEY, false);
    InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
  }

  /**
   * 当前线程启用租户
   */
  public static void enableTenant() {
    // if (isEnableTenant()) {
    //   return;
    // }
    // log.debug("启用租户过滤，线程ID: {}", Thread.currentThread().getId(), new Exception("调用栈"));
    // ThreadLocalUtil.set(IS_ENABLE_TENANT_KEY, true);
    InterceptorIgnoreHelper.clearIgnoreStrategy();
  }
}
