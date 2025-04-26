package top.zhjh.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 租户配置
 */
@ConfigurationProperties(prefix = "wuyou.tenant")
@Component
@Data
public class TenantConf {
  /**
   * 是否启用租户
   */
  private Boolean enabled = false;

  private Boolean getEnabled() {
    return enabled;
  }

  public Boolean enabled() {
    return Boolean.TRUE.equals(enabled);
  }

  public Boolean disabled() {
    return Boolean.FALSE.equals(enabled);
  }

  /**
   * 租户 ID 列名
   */
  private String tenantIdColumn = "tenant_id";
  /**
   * 忽略表名，即没有 tenant_id 的表
   */
  private List<String> ignoreTables;
}
