package top.zhjh.util;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
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
    if (expression instanceof BinaryExpression) {
      return ((BinaryExpression) expression).getLeftExpression();
    }
    if (expression instanceof InExpression) {
      return ((InExpression) expression).getLeftExpression();
    }
    if (expression instanceof TimezoneExpression) {
      return ((TimezoneExpression) expression).getLeftExpression();
    }
    if (expression instanceof CastExpression) {
      return ((CastExpression) expression).getLeftExpression();
    }
    if (expression instanceof CollateExpression) {
      return ((CollateExpression) expression).getLeftExpression();
    }
    if (expression instanceof Between) {
      return ((Between) expression).getLeftExpression();
    }
    if (expression instanceof ExcludesExpression) {
      return ((ExcludesExpression) expression).getLeftExpression();
    }
    if (expression instanceof IncludesExpression) {
      return ((IncludesExpression) expression).getLeftExpression();
    }
    if (expression instanceof IsBooleanExpression) {
      return ((IsBooleanExpression) expression).getLeftExpression();
    }
    if (expression instanceof IsNullExpression) {
      return ((IsNullExpression) expression).getLeftExpression();
    }
    return null;
  }

  public static Expression getRightExpression(Expression expression) {
    if (expression instanceof BinaryExpression) {
      return ((BinaryExpression) expression).getRightExpression();
    }
    if (expression instanceof InExpression) {
      return ((InExpression) expression).getRightExpression();
    }
    if (expression instanceof ExcludesExpression) {
      return ((ExcludesExpression) expression).getRightExpression();
    }
    if (expression instanceof IncludesExpression) {
      return ((IncludesExpression) expression).getRightExpression();
    }
    return null;
  }

  /**
   * 拼接 AND 条件
   *
   * @param left  左条件
   * @param right 右条件
   * @return AND 表达式
   */
  public static Expression and(Expression left, Expression right) {
    if (left == null) {
      return right;
    }
    if (right == null) {
      return left;
    }

    // JSqlParser 5.0+ 废弃了 Parenthesis，使用 ParenthesedExpressionList 替代
    // 它位于 operators.relational 包下，已经包含在 import net.sf.jsqlparser.expression.operators.relational.* 中
    ParenthesedExpressionList<Expression> leftP = new ParenthesedExpressionList<>();
    leftP.add(left);

    ParenthesedExpressionList<Expression> rightP = new ParenthesedExpressionList<>();
    rightP.add(right);

    return new AndExpression(leftP, rightP);
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
