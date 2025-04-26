package top.zhjh.util;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

import java.sql.SQLException;

public class JSqlParserUtil {

  public static Expression getLeftExpression(Expression expression) {
    return switch (expression) {
      case BinaryExpression binaryExpression -> binaryExpression.getLeftExpression();
      case InExpression inExpression -> inExpression.getLeftExpression();
      case TimezoneExpression timezoneExpression -> timezoneExpression.getLeftExpression();
      case CastExpression castExpression -> castExpression.getLeftExpression();
      case CollateExpression collateExpression -> collateExpression.getLeftExpression();
      case Between between -> between.getLeftExpression();
      case ExcludesExpression excludesExpression -> excludesExpression.getLeftExpression();
      case IncludesExpression includesExpression -> includesExpression.getLeftExpression();
      case IsBooleanExpression isBooleanExpression -> isBooleanExpression.getLeftExpression();
      case IsNullExpression isNullExpression -> isNullExpression.getLeftExpression();
      case null, default -> null;
    };
  }

  public static Expression getRightExpression(Expression expression) {
    return switch (expression) {
      case BinaryExpression binaryExpression -> binaryExpression.getRightExpression();
      case InExpression inExpression -> inExpression.getRightExpression();
      case ExcludesExpression excludesExpression -> excludesExpression.getRightExpression();
      case IncludesExpression includesExpression -> includesExpression.getRightExpression();
      case null, default -> null;
    };
  }

  private static MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
    MappedStatement.Builder builder =
      new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
    builder.resource(ms.getResource());
    builder.fetchSize(ms.getFetchSize());
    builder.statementType(ms.getStatementType());
    builder.keyGenerator(ms.getKeyGenerator());
    if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
      StringBuilder keyProperties = new StringBuilder();
      for (String keyProperty : ms.getKeyProperties()) {
        keyProperties.append(keyProperty).append(",");
      }
      keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
      builder.keyProperty(keyProperties.toString());
    }
    builder.timeout(ms.getTimeout());
    builder.parameterMap(ms.getParameterMap());
    builder.resultMaps(ms.getResultMaps());
    builder.resultSetType(ms.getResultSetType());
    builder.cache(ms.getCache());
    builder.flushCacheRequired(ms.isFlushCacheRequired());
    builder.useCache(ms.isUseCache());

    return builder.build();
  }

  static class BoundSqlSqlSource implements SqlSource {
    private final BoundSql boundSql;

    public BoundSqlSqlSource(BoundSql boundSql) {
      this.boundSql = boundSql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
      return boundSql;
    }
  }

  /**
   * 包装sql后，重置到invocation中
   *
   * @param invocation
   * @param sql
   * @throws SQLException
   */
  public static void resetSql2Invocation(Invocation invocation, String sql) throws SQLException {
    final Object[] args = invocation.getArgs();
    MappedStatement statement = (MappedStatement) args[0];
    Object parameterObject = args[1];
    BoundSql boundSql = statement.getBoundSql(parameterObject);
    MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSql));
    MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
    msObject.setValue("sqlSource.boundSql.sql", sql);
    args[0] = newStatement;
  }
}
