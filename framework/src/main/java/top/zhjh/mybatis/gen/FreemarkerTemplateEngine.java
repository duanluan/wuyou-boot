package top.zhjh.mybatis.gen;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import top.csaf.lang.StrUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 自定义模板引擎处理，用于生成 QO、VO 等
 */
public class FreemarkerTemplateEngine extends com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine {

  @Override
  protected void outputCustomFile(List<CustomFile> customFiles, TableInfo tableInfo, Map<String, Object> objectMap) {
    String entityName = tableInfo.getEntityName();
    String parentPath = this.getPathInfo(OutputFile.parent);
    customFiles.forEach((file) -> {
      String filePath = StrUtil.isNotBlank(file.getFilePath()) ? file.getFilePath() : parentPath;
      if (StrUtil.isNotBlank(file.getPackageName())) {
        filePath = filePath + File.separator + file.getPackageName();
        filePath = filePath.replaceAll("\\.", "\\" + File.separator);
      }

      // 自定义的包名:Entity 后缀：model/qo:GetQO
      String[] customFileNames = file.getFileName().split(":");
      String fileName = filePath + (!customFileNames[0].startsWith("/") ? File.separator : "") + customFileNames[0] + File.separator + entityName + customFileNames[1] + ".java";
      String templatePath = file.getTemplatePath();
      if (!templatePath.endsWith(".ftl")) {
        templatePath += ".ftl";
      }

      // 在路由名后加 s
      String[] controllerMappingHyphens = objectMap.get("controllerMappingHyphen").toString().split("-");
      if (controllerMappingHyphens.length > 1) {
        StringBuilder controllerMappingHyphenStr = new StringBuilder(controllerMappingHyphens[0]);
        for (int i = 1; i < controllerMappingHyphens.length; i++) {
          controllerMappingHyphenStr.append(StrUtil.capitalize(controllerMappingHyphens[i]));
        }
        objectMap.put("controllerMappingHyphen", controllerMappingHyphenStr.append("s").toString());
      }
      // 新增文件夹后缀（v3.5.1 还有的）
      objectMap.put("fileNameSuffix", customFileNames[1]);

      this.outputFile(new File(fileName), objectMap, templatePath, file.isFileOverride());
    });
  }
}
