package top.zhjh.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置
 */
@MapperScan("top.zhjh.mapper")
@Configuration
public class MyBatisConfig {

  /**
   * JDBC 驱动类
   */
  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  /**
   * 分页插件
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    DbType dbType = null;
    if (driverClassName.contains("sqlite")) {
      dbType = DbType.SQLITE;
    } else if (driverClassName.contains("mysql")) {
      dbType = DbType.MYSQL;
    }
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
    return interceptor;
  }
}
