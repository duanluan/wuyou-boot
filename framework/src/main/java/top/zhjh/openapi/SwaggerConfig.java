package top.zhjh.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi userApi() {
    // String[] packagedToMatch = {"com.xiaominfo.knife4j.demo.web"};
    return GroupedOpenApi.builder()
      .group("系统").pathsToMatch("/sys/**")
      // .addOperationCustomizer((operation, handlerMethod) -> operation.addParametersItem(new HeaderParameter().name("groupCode").example("测试").description("集团code").schema(new StringSchema()._default("BR").name("groupCode").description("集团code"))))
      // .packagesToScan(packagedToMatch)
      .build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(
      new Info()
        .title("wuyou-boot API")
        .version("1.0.0")
        // .description("")
        // 服务 URL
        .termsOfService("http://localhost:8790")
      // .license(new License().name("Apache 2.0").url("http://doc.xiaominfo.com"))
    );
  }
}
