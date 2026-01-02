package top.zhjh.service;

import org.springframework.stereotype.Service;
import top.zhjh.util.StpExtUtil;

/**
 * 安全上下文实现类
 * <p>
 * 位于 System 模块，负责实现 Framework 定义的接口，调用具体的业务工具类
 * </p>
 */
@Service
public class SecurityContextService implements SecurityContext {
  @Override
  public Long getTenantId() {
    // 调用 system 模块里的工具类
    return StpExtUtil.getTenantId();
  }

  @Override
  public boolean isSuperAdmin() {
    // 调用 system 模块里的工具类
    return StpExtUtil.isSuperAdmin();
  }
}
