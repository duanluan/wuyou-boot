package top.zhjh.mybatis.sqlinjector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 根据 ID 查询总数
 */
public class CountById extends AbstractMethod {

  public CountById() {
    this("countById");
  }

  public CountById(String name) {
    super(name);
  }

  /**
   * 参考 {@link com.baomidou.mybatisplus.core.injector.methods.SelectById}
   *
   * @param mapperClass mapper 接口
   * @param modelClass  mapper 泛型
   * @param tableInfo   数据库表反射信息
   * @return
   */
  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
    SqlSource sqlSource = super.createSqlSource(configuration, String.format(
      "SELECT COUNT(*) FROM %s WHERE %s = #{%s} %s",
      tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
      tableInfo.getLogicDeleteSql(true, true)), Object.class);
    return this.addSelectMappedStatementForOther(mapperClass, methodName, sqlSource, Long.class);
  }
}
