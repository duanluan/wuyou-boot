package top.zhjh.mybatis.wrapper;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Slf4j
@Component
public class MyWrapperInnerInterceptor implements InnerInterceptor {

  protected static final String DELIMITER = "$$$";
  protected static final String DELIMITER_ESCAPE = "\\$\\$\\$";

  // CCJSqlParserManager parserManager = new CCJSqlParserManager();

  // @Override
  // public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
  //   return InnerInterceptor.super.willDoQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
  // }

  @Override
  public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    DbType dbType = JdbcUtils.getDbType(executor);
    a:
    {
      if (dbType == null) {
        log.error("Unknown database type");
        break a;
      }
      String dbAlias = dbType.getDb();

      String sql = boundSql.getSql();
      int startIdx = sql.indexOf(DELIMITER);
      if (startIdx == -1) {
        break a;
      }
      int endIdx = sql.lastIndexOf(DELIMITER);
      // 两个分隔符之间至少有一个字符
      if (endIdx <= startIdx + DELIMITER.length() + 1) {
        break a;
      }
      // 需要替换的 SQL
      String oldSql = sql.substring(startIdx + 3, endIdx);
      String newSql = null;
      for (String dbSql : oldSql.split(DELIMITER_ESCAPE)) {
        if (dbSql.indexOf(dbAlias + ":") != 0) {
          continue;
        }
        // 新 SQL 为“数据库:”后面这段
        newSql = dbSql.substring(dbAlias.length() + 1);
      }
      if (newSql == null) {
        // 自定义 Wrapper 中的 sql 不支持此数据库
        log.error("Custom Wrapper SQL doesn't support the current database type: {}", dbAlias);
      } else {
        // try {
        //   Statement statement = parserManager.parse(new StringReader(sql));
        //   Expression where = null;
        //   if (statement instanceof Select) {
        //     where = ((Select) statement).getPlainSelect().getWhere();
        //   } else if (statement instanceof Update) {
        //     where = ((Update) statement).getWhere();
        //   } else if (statement instanceof Delete) {
        //     where = ((Delete) statement).getWhere();
        //   }
        // } catch (JSQLParserException e) {
        //   throw new RuntimeException(e);
        // }
        PluginUtils.mpBoundSql(boundSql).sql(sql.replace(DELIMITER + oldSql + DELIMITER, newSql));
      }
    }
    InnerInterceptor.super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
  }

  // @Override
  // public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
  //   return InnerInterceptor.super.willDoUpdate(executor, ms, parameter);
  // }
  //
  // @Override
  // public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
  //   InnerInterceptor.super.beforeUpdate(executor, ms, parameter);
  // }
  //
  // @Override
  // public void beforeGetBoundSql(StatementHandler sh) {
  //   InnerInterceptor.super.beforeGetBoundSql(sh);
  // }
  //
  // @Override
  // public void setProperties(Properties properties) {
  //   InnerInterceptor.super.setProperties(properties);
  // }
  //
  // @Override
  // public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
  //   InnerInterceptor.super.beforePrepare(sh, connection, transactionTimeout);
  // }
}
