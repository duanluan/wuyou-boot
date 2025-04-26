package top.zhjh.config.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.csaf.thread.ThreadLocalUtil;
import top.zhjh.prop.TenantConf;

import javax.annotation.Resource;

@Slf4j
@Component
public class TenantContext {

  private static TenantConf tenantConf;

  @Resource
  public void setTenantConf(TenantConf tenantConf) {
    TenantContext.tenantConf = tenantConf;
  }

  private static final String IS_ENABLE_TENANT_KEY = "isEnableTenant";

  /**
   * 当前线程是否启用租户，如果全局禁用，则返回 false
   *
   * @return {@code true} 已启用租户
   */
  public static boolean enabled() {
    if (tenantConf.disabled()) {
      return false;
    }
    return Boolean.TRUE.equals(ThreadLocalUtil.get(IS_ENABLE_TENANT_KEY));
  }

  /**
   * 当前线程是否禁用租户，如果全局禁用，则返回 true
   *
   * @return {@code true} 已禁用租户
   */
  public static boolean disabled() {
    if (tenantConf.disabled()) {
      return true;
    }
    return Boolean.FALSE.equals(ThreadLocalUtil.get(IS_ENABLE_TENANT_KEY));
  }

  /**
   * 当前线程禁用租户
   */
  public static void disable() {
    if (disabled()) {
      return;
    }
    ThreadLocalUtil.set(IS_ENABLE_TENANT_KEY, false);
  }

  /**
   * 当前线程启用租户
   */
  public static void enable() {
    if (enabled()) {
      return;
    }
    ThreadLocalUtil.set(IS_ENABLE_TENANT_KEY, true);
  }

  /**
   * 清除租户上下文
   */
  public static void clear() {
    ThreadLocalUtil.remove(IS_ENABLE_TENANT_KEY);
  }
}
