package top.zhjh.mvc;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.support.spring.webservlet.view.FastJsonJsonView;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.csaf.charset.StandardCharsets;

import java.util.Collections;
import java.util.List;

import static com.alibaba.fastjson2.JSONReader.Feature.*;
import static com.alibaba.fastjson2.JSONWriter.Feature.WriteLongAsString;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * 使用 FastJsonHttpMessageConverter 来替换 Spring MVC 默认的 HttpMessageConverter 以提高 @RestController @ResponseBody @RequestBody 注解的 JSON 序列化和反序列化速度
   * <a href="https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md#21--spring-web-mvc-converter=">https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md#21--spring-web-mvc-converter=</a>
   *
   * @param converters HTTP 消息转换器
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
    FastJsonConfig config = new FastJsonConfig();
    // 时间格式化
    // config.setDateFormat("yyyy-MM-dd HH:mm:ss");
    // 通过 Features 配置序列化和反序列化的行为：https://alibaba.github.io/fastjson2/features_cn.html
    // 反序列化：转对象
    config.setReaderFeatures(
      // 基于非 static 的 field（包括 private）做反序列化
      FieldBased,
      // 支持自动类型
      SupportAutoType,
      // 支持数据映射的方式
      SupportArrayToBean
    );
    // 序列化：转 JSON
    config.setWriterFeatures(
      // // 序列化枚举值为前端返回值：https://baomidou.com/guides/auto-convert-enum
      // JSONWriter.Feature.WriteEnumUsingToString
      // 将 Long 序列化为 String
      WriteLongAsString
    );
    converter.setFastJsonConfig(config);
    converter.setDefaultCharset(StandardCharsets.UTF_8);
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    converters.add(0, converter);
  }

  /**
   * 使用 FastJsonJsonView 来设置 Spring MVC 默认的视图模型解析器，以提高 @Controller @ResponseBody ModelAndView JSON 序列化速度
   *
   * @param registry 视图解析器注册器
   */
  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
    //自定义配置...
    //FastJsonConfig config = new FastJsonConfig();
    //config.set...
    //fastJsonJsonView.setFastJsonConfig(config);
    registry.enableContentNegotiation(fastJsonJsonView);
  }

  /**
   * 添加跨域请求
   *
   * @param registry 跨域请求注册器
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      // 允许跨域访问
      .allowCredentials(true)
      // 允许跨域访问的域名
      .allowedOriginPatterns("*")
      // 允许跨域访问的方法
      .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
      // 允许跨域访问的请求头
      .allowedHeaders("*");
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    // 接口入参枚举值转换为枚举类型
    registry.addConverterFactory(new EnumConverterFactory());
  }
}
