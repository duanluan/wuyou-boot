package top.zhjh.config.datascope;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import top.csaf.coll.CollUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.enums.DataScopeActionType;
import top.zhjh.enums.DataScopeType;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.entity.SysRoleDept;
import top.zhjh.model.entity.SysUser;
import top.zhjh.service.SysRoleDeptService;
import top.zhjh.service.SysRoleService;
import top.zhjh.service.SysUserService;
import top.zhjh.util.JSqlParserUtil;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MyBatis 数据权限拦截器
 * <p>
 * 基于 JSqlParser 5.1+ 实现，解决了 N+1 查询问题
 * </p>
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
public class DataScopeInterceptor implements Interceptor {

  @Lazy
  @Resource
  private SysUserService sysUserService;
  @Lazy
  @Resource
  private SysRoleService sysRoleService;
  @Lazy
  @Resource
  private SysRoleDeptService sysRoleDeptService;

  /**
   * 获取 Mapper 方法上的 @DataScope 注解
   */
  private DataScope findDataScopeAnnotation(MappedStatement mappedStatement) {
    try {
      String id = mappedStatement.getId();
      String className = id.substring(0, id.lastIndexOf("."));
      String methodName = id.substring(id.lastIndexOf(".") + 1);
      // 处理 MyBatis-Plus 分页插件自动生成的 count 查询 (_mpCount)
      if (methodName.endsWith("_mpCount")) {
        methodName = methodName.substring(0, methodName.length() - 8);
      }
      Class<?> clazz = Class.forName(className);
      // 简单处理，遍历方法查找（未处理重载情况）
      for (Method method : clazz.getMethods()) {
        if (method.getName().equals(methodName) && method.isAnnotationPresent(DataScope.class)) {
          return method.getAnnotation(DataScope.class);
        }
      }
    } catch (Exception e) {
      log.warn("查找 @DataScope 注解失败: {}", e.getMessage());
    }
    return null;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

    // 1. 优先检查是否有 @DataScope 注解
    DataScope dataScope = this.findDataScopeAnnotation(mappedStatement);
    if (dataScope == null) {
      return invocation.proceed();
    }

    // 2. 如果是超级管理员，直接放行
    // 加一层 isLogin 判断，防止未登录时调用 isSuperAdmin 抛出异常
    if (StpUtil.isLogin() && StpExtUtil.isSuperAdmin()) {
      return invocation.proceed();
    }

    // 3. 构造数据权限过滤 SQL
    // 判断是查询还是增删改
    boolean isQuery = mappedStatement.getSqlCommandType() == SqlCommandType.SELECT;
    String scopeSql = getScopeSql(dataScope, isQuery);

    // 如果返回空串，说明拥有全部权限或无限制，直接放行
    if (StrUtil.isBlank(scopeSql)) {
      return invocation.proceed();
    }

    // 4. 利用 JSqlParser 注入 SQL
    Object[] args = invocation.getArgs();
    Object parameter = args[1];
    BoundSql boundSql;

    if (isQuery) {
      RowBounds rowBounds = (RowBounds) args[2];
      Executor executor = (Executor) invocation.getTarget();
      CacheKey cacheKey;

      if (args.length == 4) {
        boundSql = mappedStatement.getBoundSql(parameter);
        cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
      } else {
        cacheKey = (CacheKey) args[4];
        boundSql = (BoundSql) args[5];
      }

      Select select = (Select) CCJSqlParserUtil.parse(boundSql.getSql());
      this.setSelectDataScope(select, scopeSql);

      BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), select.toString(), boundSql.getParameterMappings(), parameter);

      // 复制 additionalParameters，防止 foreach 等标签报错
      for (ParameterMapping mapping : boundSql.getParameterMappings()) {
        String prop = mapping.getProperty();
        if (boundSql.hasAdditionalParameter(prop)) {
          newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
        }
      }

      return executor.query(mappedStatement, parameter, rowBounds, (ResultHandler) args[3], cacheKey, newBoundSql);
    } else {
      // Update / Delete
      boundSql = mappedStatement.getBoundSql(parameter);
      Statement statement = CCJSqlParserUtil.parse(boundSql.getSql());
      Expression condition = CCJSqlParserUtil.parseCondExpression(scopeSql);

      if (statement instanceof Update update) {
        Expression where = update.getWhere();
        update.setWhere(JSqlParserUtil.and(where, condition));
      } else if (statement instanceof Delete delete) {
        Expression where = delete.getWhere();
        delete.setWhere(JSqlParserUtil.and(where, condition));
      }

      JSqlParserUtil.resetSql2Invocation(invocation, statement.toString());
      return invocation.proceed();
    }
  }

  /**
   * 处理 Select 语句注入
   */
  private void setSelectDataScope(Select select, String scopeSql) {
    Expression condition;
    try {
      // 使用 JSqlParser 解析条件字符串，自动处理括号优先级
      condition = CCJSqlParserUtil.parseCondExpression(scopeSql);
    } catch (Exception e) {
      log.error("解析数据权限 SQL 失败: {}", scopeSql, e);
      return;
    }

    if (select instanceof PlainSelect plainSelect) {
      Expression where = plainSelect.getWhere();
      plainSelect.setWhere(JSqlParserUtil.and(where, condition));
    } else if (select instanceof SetOperationList setOperationList) {
      for (Select subSelect : setOperationList.getSelects()) {
        setSelectDataScope(subSelect, scopeSql);
      }
    }
  }

  /**
   * 获取数据权限 SQL 片段
   *
   * @param dataScope 注解配置
   * @param isQuery   true: 查询权限, false: 增删改权限
   * @return 过滤 SQL，如 "(t.dept_id IN (1,2) OR t.created_by = 1001)"
   */
  private String getScopeSql(DataScope dataScope, boolean isQuery) {
    Long userId = StpUtil.getLoginIdAsLong();
    SysUser user = sysUserService.getById(userId);
    if (user == null) {
      return "1 = 0"; // 用户不存在
    }

    List<SysRole> roles = sysRoleService.listByIds(user.getRoleIds());
    if (CollUtil.isEmpty(roles)) {
      return "1 = 0"; // 无角色
    }

    // 权限聚合容器
    Set<Long> exactDeptIds = new HashSet<>();       // 精确匹配的部门 (IN 1,2)
    Set<Long> withChildrenDeptIds = new HashSet<>(); // 包含子级的部门 (IN SELECT... LIKE)
    boolean isAll = false;
    boolean isSelf = false;

    // 预先批量查询“自定义”权限的部门，减少数据库交互
    DataScopeActionType actionType = isQuery ? DataScopeActionType.QUERY : DataScopeActionType.UPDATE;
    // 找出所有是“自定义”类型的角色ID
    List<Long> customScopeRoleIds = roles.stream()
      .filter(r -> DataScopeType.CUSTOM.equals(isQuery ? r.getQueryDataScope() : r.getUpdateDataScope()))
      .map(SysRole::getId)
      .collect(Collectors.toList());

    List<SysRoleDept> allCustomRoleDepts = new ArrayList<>();
    if (CollUtil.isNotEmpty(customScopeRoleIds)) {
      allCustomRoleDepts = sysRoleDeptService.lambdaQuery()
        .select(SysRoleDept::getRoleId, SysRoleDept::getDeptId, SysRoleDept::getDataScopeActionType)
        .in(SysRoleDept::getRoleId, customScopeRoleIds)
        .eq(SysRoleDept::getDataScopeActionType, actionType)
        .list();
    }

    for (SysRole role : roles) {
      DataScopeType scopeType = isQuery ? role.getQueryDataScope() : role.getUpdateDataScope();
      if (scopeType == null) {
        continue;
      }

      switch (scopeType) {
        case ALL -> isAll = true;
        // 在内存中匹配当前角色的部门
        case CUSTOM -> allCustomRoleDepts.stream()
          .filter(rd -> rd.getRoleId().equals(role.getId()))
          .forEach(rd -> exactDeptIds.add(rd.getDeptId()));
        // 本部门及以下：需要加入到子查询集合
        case CURRENT_DEPT_AND_CHILDREN -> {
          if (CollUtil.isNotEmpty(user.getDeptIds())) {
            withChildrenDeptIds.addAll(user.getDeptIds());
            // 为了保险起见（防止 ancestors 逻辑未包含自身），也将自身ID加入精确匹配
            exactDeptIds.addAll(user.getDeptIds());
          }
        }
        case CURRENT_DEPT -> {
          if (CollUtil.isNotEmpty(user.getDeptIds())) {
            exactDeptIds.addAll(user.getDeptIds());
          }
        }
        case ONLY_SELF -> isSelf = true;
      }

      if (isAll) break; // 拥有全部权限，直接跳出
    }

    if (isAll) {
      return ""; // 不拼接 SQL，查询全部
    }

    // 开始拼接 SQL 条件
    List<String> conditions = new ArrayList<>();
    String deptAlias = StrUtil.isNotBlank(dataScope.deptAlias()) ? dataScope.deptAlias() + "." : "";
    String deptField = dataScope.deptField();
    String userAlias = StrUtil.isNotBlank(dataScope.userAlias()) ? dataScope.userAlias() + "." : "";
    String userField = dataScope.userField();

    // 1. 部门精确匹配 (dept_id IN (1, 2))
    if (CollUtil.isNotEmpty(exactDeptIds)) {
      conditions.add(deptAlias + deptField + " IN (" + StrUtil.join(exactDeptIds, ",") + ")");
    }

    // 2. 部门子级匹配 (利用子查询优化 N+1)
    // SQL: dept_id IN (SELECT id FROM sys_dept WHERE ancestors LIKE '%,100,%' OR ...)
    if (CollUtil.isNotEmpty(withChildrenDeptIds)) {
      // 构造 OR 条件: ancestors LIKE '%,id,%'
      // 假设 ancestors 格式为 "0,100,101" (逗号分隔)，为避免匹配错误（如 1 匹配到 11），应匹配 ",id,"
      // 但 ancestors 字段通常不含首尾逗号，这里假定数据库值形如 "0,100,101"
      // 使用 FIND_IN_SET 是最准确的，但 LIKE 性能更好且更通用。
      // 这里生成: (ancestors LIKE '%,id,%' OR ancestors LIKE 'id,%' OR ancestors LIKE '%,id' OR ancestors = 'id')
      // 简化处理：大部分设计 ancestors 存 "0,100,101"，匹配子级通常是 LIKE '0,100,101,%' (前缀匹配) 或 LIKE '%,id,%'
      // 根据您的提示：含义为 TopId,...,SelfId。
      // 如果要查 DeptA(id=100) 的子级，子级的 ancestors 必然包含 "...,100,..."。

      List<String> likeConditions = new ArrayList<>();
      for (Long id : withChildrenDeptIds) {
        // 匹配逻辑：ancestors 中包含当前 ID
        // 注意：SysDept 表名硬编码为 sys_dept，如需灵活可读配置
        likeConditions.add("ancestors LIKE '%," + id + ",%'");
        // 兼容 ID 在末尾或开头的情况（视具体数据结构而定，加逗号是最稳妥的防误判方式）
        likeConditions.add("ancestors LIKE '%," + id + "'");
        likeConditions.add("ancestors LIKE '" + id + ",%'");
        likeConditions.add("ancestors = '" + id + "'");
      }

      String subQuery = deptAlias + deptField + " IN (SELECT id FROM sys_dept WHERE " +
        "(" + StrUtil.join(likeConditions, " OR ") + "))";
      conditions.add(subQuery);
    }

    // 3. 仅本人 (created_by = user_id)
    if (isSelf) {
      conditions.add(userAlias + userField + " = " + userId);
    }

    if (CollUtil.isEmpty(conditions)) {
      // 没有任何权限
      return "1 = 0";
    }

    // 将所有条件用 OR 连接，并用括号包裹
    // 例如：( dept_id IN (10) OR dept_id IN (SELECT...) OR created_by = 1 )
    return "(" + StrUtil.join(conditions, " OR ") + ")";
  }
}