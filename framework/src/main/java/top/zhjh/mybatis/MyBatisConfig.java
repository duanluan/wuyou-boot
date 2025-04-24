package top.zhjh.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.zhjh.mybatis.wrapper.MyWrapperInnerInterceptor;

/**
 * MyBatis 配置
 */
@MapperScan("top.zhjh.mapper")
@Configuration
public class MyBatisConfig {

  /**
   * MyBatis Plus 拦截器
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 分页插件
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
    // 自定义 Wrapper 拦截器
    interceptor.addInnerInterceptor(new MyWrapperInnerInterceptor());
    return interceptor;
  }
}
