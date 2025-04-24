package top.zhjh.mybatis.sqlinjector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义 SQL 注入器
 */
@Slf4j
@Component
public class MySqlInjector extends DefaultSqlInjector {

  /**
   * 获取需要注入的方法列表
   *
   * @param configuration 配置对象
   * @param mapperClass   当前mapper
   * @param tableInfo     表信息
   * @return 需要注入的方法列表
   */
  @Override
  public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
    List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
    // 是否有主键
    if (tableInfo.havePK()) {
      methodList.add(new CountById());
    } else {
      log.warn("{} not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.", tableInfo.getEntityType());
    }
    return methodList;
  }
}
