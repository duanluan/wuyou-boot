package top.zhjh.config.tenant;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.csaf.coll.CollUtil;
import top.zhjh.prop.TenantConf;
import top.zhjh.util.JSqlParserUtil;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * MyBatis 租户拦截器
 */
@Slf4j
@Component
@Intercepts({
  // 普通查询
  @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
  // 缓存命中时的查询
  @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
  // 插入更新删除
  @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class TenantInterceptor implements Interceptor {

  @Resource
  private TenantConf tenantConf;

  /**
   * 登录用户是否跳过租户拦截
   *
   * @return {@code true} 跳过租户拦截
   */
  private boolean ignoreCurrentUser() {
    return StpExtUtil.isSuperAdmin();
  }

  @Resource
  private RequestMappingHandlerMapping handlerMapping;

  /**
   * 请求方法或类上是否有 @SaIgnore 注解
   *
   * @return {@code true} 请求方法或类上有 @SaIgnore 注解
   */
  private boolean hasSaIgnore() {
    try {
      // 获取当前请求
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
      if (requestAttributes == null) {
        return false;
      }
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
      HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);
      if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        // 检查方法上是否有SaIgnore注解
        SaIgnore saIgnore = method.getAnnotation(SaIgnore.class);
        if (saIgnore != null) {
          return true;
        }
        // 检查类上是否有SaIgnore注解
        saIgnore = method.getDeclaringClass().getAnnotation(SaIgnore.class);
        return saIgnore != null;
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return false;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    try {
      // 接口方法或类上是否有 @SaIgnore 注解，有的话忽略租户拦截
      if (this.hasSaIgnore()) {
        return invocation.proceed();
      }
      // 全局配置是否禁用租户拦截
      if (tenantConf.disabled()) {
        return invocation.proceed();
      }
      // 租户上下文是否禁用租户拦截
      if (TenantContext.disabled()) {
        return invocation.proceed();
      }
      // 是否忽略登录用户
      if (this.ignoreCurrentUser()) {
        return invocation.proceed();
      }

      Object[] args = invocation.getArgs();
      MappedStatement mappedStatement = (MappedStatement) args[0];
      Object parameter = args[1];

      if ("query".equals(invocation.getMethod().getName())) {
        RowBounds rowBounds = (RowBounds) args[2];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        // 4 个参数时
        if (args.length == 4) {
          boundSql = mappedStatement.getBoundSql(parameter);
          cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        }
        // 6 个参数时
        else {
          cacheKey = (CacheKey) args[4];
          boundSql = (BoundSql) args[5];
        }

        // 加入租户条件
        Select select = (Select) CCJSqlParserUtil.parse(boundSql.getSql());
        this.handleSelect(select);
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), select.toString(), boundSql.getParameterMappings(), parameter);

        // 复制原始 BoundSql 中的 additionalParameters 到新的 BoundSql 中，否则会报错“org.apache.ibatis.reflection.ReflectionException: There is no getter for property named '__frch_……”
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
          String prop = mapping.getProperty();
          if (boundSql.hasAdditionalParameter(prop)) {
            newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
          }
        }

        return executor.query(mappedStatement, parameter, rowBounds, (ResultHandler) args[3], cacheKey, newBoundSql);
      } else {
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Statement statement = CCJSqlParserUtil.parse(boundSql.getSql());
        // 加入租户条件
        switch (statement) {
          case Insert insert -> {
            for (Expression expression : insert.getValues().getExpressions()) {
              if (expression instanceof ParenthesedSelect) {
                this.handleSelect(this.getPlainSelect((ParenthesedSelect) expression));
              } else if (expression instanceof SetOperationList) {
                this.handleSelect((SetOperationList) expression);
              }
            }
          }
          case Update update -> {
            this.handleFromItem(update.getFromItem(), null);
            this.handleJoins(update.getJoins());
            this.handleWhere(update.getWhere());
          }
          case Delete delete -> {
            this.handleJoins(delete.getJoins());
            this.handleWhere(delete.getWhere());
          }
          case null, default -> log.warn("Unsupported tenant SQL statement: {}", statement);
        }

        JSqlParserUtil.resetSql2Invocation(invocation, statement.toString());
        return invocation.proceed();
      }
    } catch (Exception e) {
      log.error("租户拦截异常：{}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * 指定表是否跳过租户拦截
   *
   * @param tableName 表名
   * @return {@code true} 跳过租户拦截
   */
  private boolean ignoreTable(String tableName) {
    return CollUtil.contains(tenantConf.getIgnoreTables(), tableName);
  }

  /**
   * 处理查询中的子查询 或 setOperationList（union、union all 等）
   *
   * @param select 查询
   */
  private void handleSelect(Select select) {
    if (select instanceof PlainSelect plainSelect) {
      this.handlePlainSelect(plainSelect);
    } else if (select instanceof SetOperationList setOperationList) {
      for (Select select1 : setOperationList.getSelects()) {
        this.handleSelect(select1);
      }
    }
  }

  /**
   * 获取 PlainSelect，有可能 parenthesedSelect 获取到的 select 还是 ParenthesedSelect，比如“…… values ((select ……”
   *
   * @param parenthesedSelect 子查询
   * @return PlainSelect
   */
  private PlainSelect getPlainSelect(ParenthesedSelect parenthesedSelect) {
    Select select = parenthesedSelect.getSelect();
    if (select instanceof PlainSelect) {
      return (PlainSelect) select;
    }
    return this.getPlainSelect((ParenthesedSelect) select);
  }

  /**
   * 处理查询中的子查询
   *
   * @param plainSelect 查询
   */
  private void handlePlainSelect(PlainSelect plainSelect) {
    List<SelectItem<?>> selectItems = plainSelect.getSelectItems();
    for (SelectItem<?> selectItem : selectItems) {
      // selectItem 中的子查询
      Expression expression = selectItem.getExpression();
      if (expression instanceof ParenthesedSelect) {
        // 递归子查询
        this.handlePlainSelect(this.getPlainSelect((ParenthesedSelect) expression));
      }
    }

    // from 中的子查询
    handleFromItem(plainSelect.getFromItem(), plainSelect);
    // join 中的子查询
    this.handleJoins(plainSelect.getJoins());
    // where 中的子查询
    this.handleWhere(plainSelect.getWhere());
  }

  /**
   * 处理 from 中的子查询
   *
   * @param fromItem    from 条件
   * @param plainSelect 查询
   */
  private void handleFromItem(FromItem fromItem, PlainSelect plainSelect) {
    if (fromItem instanceof ParenthesedSelect) {
      this.handlePlainSelect(this.getPlainSelect((ParenthesedSelect) fromItem));
    } else if (fromItem instanceof Table table) {
      // 是否忽略表
      if (ignoreTable(table.getName())) {
        return;
      }

      // where 加入租户条件
      Expression where = plainSelect.getWhere();
      EqualsTo tenantCondition = new EqualsTo();
      tenantCondition.setLeftExpression(new Column(tenantConf.getTenantIdColumn()));
      tenantCondition.setRightExpression(new LongValue(StpExtUtil.getTenantId()));
      if (where != null) {
        AndExpression newCondition = new AndExpression(where, tenantCondition);
        plainSelect.setWhere(newCondition);
      } else {
        plainSelect.setWhere(tenantCondition);
      }
    }
  }

  /**
   * 处理 join 中的子查询
   *
   * @param joins join 列表
   */
  private void handleJoins(List<Join> joins) {
    if (CollUtil.isEmpty(joins)) {
      return;
    }
    for (Join join : joins) {
      FromItem fromItem = join.getFromItem();
      // join 中的子查询
      this.handleFromItem(fromItem, null);
      // join 中 on 的子查询
      for (Expression on : join.getOnExpressions()) {
        this.handleWhere(on);
      }
    }
  }

  /**
   * 处理 where 中的子查询
   *
   * @param expression where 条件
   */
  private void handleWhere(Expression expression) {
    if (expression == null) {
      return;
    }
    if (expression instanceof ParenthesedSelect) {
      this.handlePlainSelect(this.getPlainSelect((ParenthesedSelect) expression));
      return;
    }
    Expression leftExpression = JSqlParserUtil.getLeftExpression(expression);
    Expression rightExpression = JSqlParserUtil.getRightExpression(expression);
    if (leftExpression != null) {
      // 递归 where
      this.handleWhere(leftExpression);
    }
    if (rightExpression != null) {
      this.handleWhere(rightExpression);
    }
  }
}
