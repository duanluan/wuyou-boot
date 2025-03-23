package top.zhjh.mybatis.sqlinjector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义 SQL 注入器
 */
@Component
public class MySqlInjector extends DefaultSqlInjector {

  /**
   * 参考 {@link DefaultSqlInjector#getMethodList(Class, TableInfo)}
   *
   * @param mapperClass 当前mapper
   * @param tableInfo   数据库表反射信息
   * @return
   */
  @Override
  public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
    List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
    if (tableInfo.havePK()) {
      methodList.add(new CountById());
    } else {
      logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.", tableInfo.getEntityType()));
    }
    return methodList;
  }
}
