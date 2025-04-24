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
   * 忽略表名，即没有 tenant_id 的表
   */
  private List<String> ignoreTables;
}
