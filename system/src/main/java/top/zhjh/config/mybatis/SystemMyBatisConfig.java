package top.zhjh.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.zhjh.config.mybatis.handler.MyTenantHandler;

import javax.annotation.Resource;

@Configuration
public class SystemMyBatisConfig {

  @Resource
  private MyTenantHandler myTenantHandler;

  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor1() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 自定义的租户处理器注入到 TenantLineInnerInterceptor 中：https://baomidou.com/plugins/tenant/#%E6%AD%A5%E9%AA%A4-2%E5%B0%86%E7%A7%9F%E6%88%B7%E5%A4%84%E7%90%86%E5%99%A8%E6%B3%A8%E5%85%A5%E6%8F%92%E4%BB%B6
    TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
    tenantInterceptor.setTenantLineHandler(myTenantHandler);
    interceptor.addInnerInterceptor(tenantInterceptor);
    return interceptor;
  }
}
