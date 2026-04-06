package top.zhjh.mybatis.wrapper;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import static top.zhjh.mybatis.wrapper.MyWrapperInnerInterceptor.DELIMITER;

/**
 * 此类中的扩展方法，会使用“$$$”作为自定义 SQL 的开头结尾和不同数据库 SQL 间的分割，应避免其他查询方法中使用到
 * <p>
 * 语法：$$$mysql:xxx$$$oracle:xxx$$$
 *
 * @param <T>
 */
public class MyLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {

  public MyLambdaQueryWrapper<T> jsonContains(SFunction<T, ?> column, Object val) {
    if (val == null) {
      return this;
    }
    String valueExpr = "CAST({0} AS JSON)";
    if (val instanceof String str) {
      String trimmed = str.trim();
      if (!(trimmed.startsWith("{") || trimmed.startsWith("[") || trimmed.startsWith("\""))) {
        valueExpr = "JSON_QUOTE({0})";
      }
    }
    this.apply(
      DELIMITER +
        DbType.MYSQL.getDb() + ":JSON_CONTAINS(" + super.columnsToString(column) + ", " + valueExpr + ")" +
        DELIMITER +
        DbType.MARIADB.getDb() + ":JSON_CONTAINS(" + super.columnsToString(column) + ", " + valueExpr + ")" +
        DELIMITER,
      val);
    return this;
  }

  public MyLambdaQueryWrapper<T> jsonContains(boolean condition, SFunction<T, ?> column, Object val) {
    if (!condition) {
      return this;
    }
    return jsonContains(column, val);
  }
}
