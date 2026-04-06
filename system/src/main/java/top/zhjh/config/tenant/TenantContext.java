package top.zhjh.config.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.csaf.thread.ThreadLocalUtil;
import top.zhjh.prop.TenantConf;

import javax.annotation.Resource;
import java.util.function.Supplier;

/**
 * 租户上下文（线程级）控制类。
 * <p>
 * 通过 ThreadLocal 保存“当前线程是否启用租户”的状态。
 * 全局配置关闭时，一律视为禁用；
 * 未设置线程状态时，表示“使用默认行为”（由 {@link TenantConf} 决定）。
 * </p>
 */
@Slf4j
@Component
public class TenantContext {

  /**
   * 全局租户配置。
   */
  private static TenantConf tenantConf;

  @Resource
  public void setTenantConf(TenantConf tenantConf) {
    TenantContext.tenantConf = tenantConf;
  }

  /**
   * ThreadLocal 中保存租户状态的 key。
   */
  private static final String IS_ENABLE_TENANT_KEY = "isEnableTenant";

  /**
   * 当前线程是否启用租户。
   * <p>
   * 全局禁用时恒为 false；
   * 只有当 ThreadLocal 显式设置为 true 且全局启用时，才返回 true。
   * </p>
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
   * 当前线程是否禁用租户。
   * <p>
   * 全局禁用时恒为 true；
   * 只有当 ThreadLocal 显式设置为 false 时，才返回 true。
   * </p>
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
   * 当前线程禁用租户（仅影响当前线程）。
   * <p>
   * 调用后，租户拦截器会跳过当前线程后续 SQL，直到 {@link #clear()} 或恢复。
   * </p>
   */
  public static void disable() {
    if (disabled()) {
      return;
    }
    ThreadLocalUtil.set(IS_ENABLE_TENANT_KEY, false);
  }

  /**
   * 当前线程启用租户（仅影响当前线程）。
   * <p>
   * 调用后，租户拦截器会对当前线程后续 SQL 生效。
   * </p>
   */
  public static void enable() {
    if (enabled()) {
      return;
    }
    ThreadLocalUtil.set(IS_ENABLE_TENANT_KEY, true);
  }

  /**
   * 清除租户上下文（仅影响当前线程）。
   * <p>
   * 清除后将回到“未设置线程状态”的默认行为。
   * </p>
   */
  public static void clear() {
    ThreadLocalUtil.remove(IS_ENABLE_TENANT_KEY);
  }

  /**
   * 在当前线程中临时禁用租户，执行完毕后恢复原状态。
   *
   * @param action 需在“禁用租户”状态下执行的逻辑
   */
  public static void runWithoutTenant(Runnable action) {
    boolean prevEnabled = enabled();
    boolean prevDisabled = disabled();
    disable();
    try {
      action.run();
    } finally {
      restore(prevEnabled, prevDisabled);
    }
  }

  /**
   * 在当前线程中临时禁用租户并返回结果，执行完毕后恢复原状态。
   *
   * @param action 需在“禁用租户”状态下执行的逻辑
   * @param <T>    返回值类型
   * @return 执行结果
   */
  public static <T> T supplyWithoutTenant(Supplier<T> action) {
    boolean prevEnabled = enabled();
    boolean prevDisabled = disabled();
    disable();
    try {
      return action.get();
    } finally {
      restore(prevEnabled, prevDisabled);
    }
  }

  /**
   * 根据进入临界区前的状态恢复 ThreadLocal。
   *
   * @param prevEnabled 进入前是否启用租户
   * @param prevDisabled 进入前是否禁用租户
   */
  private static void restore(boolean prevEnabled, boolean prevDisabled) {
    if (prevEnabled) {
      enable();
      return;
    }
    if (prevDisabled) {
      disable();
      return;
    }
    clear();
  }
}
