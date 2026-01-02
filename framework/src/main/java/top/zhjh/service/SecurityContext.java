package top.zhjh.service;

/**
 * 安全上下文接口
 * <p>
 * 用于打破 framework 对 system 的依赖，由 system 模块实现具体逻辑
 * </p>
 */
public interface SecurityContext {

  /**
   * 获取当前租户ID
   * @return 租户ID
   */
  Long getTenantId();

  /**
   * 判断当前是否为超级管理员
   * @return true 是超管
   */
  boolean isSuperAdmin();
}