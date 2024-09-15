package top.zhjh.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import top.csaf.io.FileUtil;
import top.csaf.yaml.YamlUtil;
import top.zhjh.base.BaseController;
import top.zhjh.base.model.BaseEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 代码生成器
 */
@Component
public class CodeGenerator {

  private static final String DB_URL;
  private static final String DB_USERNAME;
  private static final String DB_PASSWORD;
  private static final String TABLE_PREFIX;
  private static final String OUTPUT_DIR = FileUtil.getProjectPath() + "/generator/src/main/java";

  static {
    Map<String, Object> yamlMap = YamlUtil.load(FileUtil.getProjectPath() + "/admin/src/main/resources/application.yml");

    Object urlObj = YamlUtil.get(yamlMap, "spring.datasource.url");
    DB_URL = urlObj != null ? urlObj.toString() : "";
    Object usernameObj = YamlUtil.get(yamlMap, "spring.datasource.username");
    DB_USERNAME = usernameObj != null ? usernameObj.toString() : "";
    Object passwordObj = YamlUtil.get(yamlMap, "spring.datasource.password");
    DB_PASSWORD = passwordObj != null ? passwordObj.toString() : "";
    Object prefixObj = YamlUtil.get(yamlMap, "mybatis-plus.global-config.db-config.table-prefix");
    TABLE_PREFIX = prefixObj != null ? prefixObj.toString() : "";
  }

  /**
   * 读取控制台输入内容
   */
  private static final Scanner SCANNER = new Scanner(System.in);

  /**
   * 控制台输入内容读取并打印提示信息
   *
   * @param message 提示信息
   * @return 输入内容
   */
  public static String scannerNext(String message) {
    System.out.println(message);
    String nextLine = SCANNER.nextLine();
    if (StringUtils.isBlank(nextLine)) {
      // 如果输入空行继续等待
      return SCANNER.next();
    }
    return nextLine;
  }

  protected static <T> T configBuilder(IConfigBuilder<T> configBuilder) {
    return null == configBuilder ? null : configBuilder.build();
  }

  public static void main(String[] args) throws IOException {
    // 自定义模板，key 为“自定义的包名:Entity 后缀”，value 为模板路径
    Map<String, String> customFile = new HashMap<>(8);
    customFile.put("model/qo:GetQO", "/generator/entityQO.java");
    customFile.put("model/qo:PageQO", "/generator/entityQO.java");
    customFile.put("model/qo:SaveQO", "/generator/entityQO.java");
    customFile.put("model/qo:UpdateQO", "/generator/entityQO.java");
    customFile.put("model/qo:RemoveQO", "/generator/entityQO.java");

    customFile.put("model/vo:PageVO", "/generator/entityVO.java");
    customFile.put("model/vo:GetVO", "/generator/entityVO.java");

    customFile.put("struct:Struct", "/generator/struct.java");

    // 删除之前生成的文件
    FileUtil.deleteDirectory(new File(OUTPUT_DIR));

    // 代码生成器
    new AutoGenerator(configBuilder(new DataSourceConfig.Builder(DB_URL, DB_USERNAME, DB_PASSWORD)))
      // 全局配置
      .global(configBuilder(new GlobalConfig.Builder()
        // 禁用打开生成目录
        .disableOpenDir()
        // 输出目录，默认 windows: D://  linux or mac: /tmp
        .outputDir(OUTPUT_DIR)
        // 作者，默认无
        .author("ZhongJianhao")
        // 注释时间（@since），默认 yyyy-MM-dd
        .commentDate("")
        // 开启 swagger 模式，默认 false
        .enableSwagger()
      ))
      // 包配置
      .packageInfo(configBuilder(new PackageConfig.Builder()
        // 模块名
        .moduleName("")
        // 实体包名
        .entity("model.entity")
        // 父包名
        .parent("top.zhjh")
      ))
      // 自定义配置
      .injection(configBuilder(new InjectionConfig.Builder()
        .beforeOutputFile((tableInfo, stringObjectMap) -> {
          // 不启用 @TableName 注解
          // tableInfo.setConvert(false);

          // 自定义 Mapper XML 生成目录
          ConfigBuilder config = (ConfigBuilder) stringObjectMap.get("config");
          Map<OutputFile, String> pathInfoMap = config.getPathInfo();
          pathInfoMap.put(OutputFile.xml, pathInfoMap.get(OutputFile.xml).replaceAll("/java.*", "/resources/mapper"));
          stringObjectMap.put("config", config);
        })
        // 自定义文件，比如 VO
        .customFile(customFile)
      ))
      // 策略配置
      .strategy(configBuilder(new StrategyConfig.Builder()
        // 表名
        .addInclude(scannerNext("请输入表名（英文逗号分隔）：").split(","))
        // 过滤表前缀
        .addTablePrefix(TABLE_PREFIX)

        // Entity 策略
        .entityBuilder()
        // 开启 Lombok 模式
        .enableLombok()
        // 禁用生成 serialVersionUID
        .disableSerialVersionUID()
        // 数据库表映射到实体的命名策略：下划线转驼峰
        .naming(NamingStrategy.underline_to_camel)
        // 主键策略为自增，默认 IdType.AUTO
        .idType(IdType.INPUT)
        // 父类
        .superClass(BaseEntity.class)
        // 覆盖已有文件
        .enableFileOverride()

        // Controller 策略
        .controllerBuilder()
        // 生成 @RestController 注解
        .enableRestStyle()
        // 父类
        .superClass(BaseController.class)
        .enableFileOverride()

        // Service 策略
        .serviceBuilder()
        .enableFileOverride()

        // Mapper 策略
        .mapperBuilder()
        .enableFileOverride()
      ))
      // 模板配置
      .template(configBuilder(new TemplateConfig.Builder()
        // 自定义模板：https://github.com/baomidou/generator/tree/develop/mybatis-plus-generator/src/main/resources/templates
        .entity("/generator/entity.java")
        .mapper("/generator/mapper.java")
        .service("/generator/service.java")
        .serviceImpl("/generator/serviceImpl.java")
        .controller("/generator/controller.java")
      ))

      // 执行并指定模板引擎
      .execute(new FreemarkerTemplateEngine());
  }
}
