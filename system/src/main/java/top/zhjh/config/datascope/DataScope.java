package top.zhjh.config.datascope;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * <p>
 * 在 Mapper 方法上使用，DataScopeInterceptor 会自动拦截并注入 SQL 过滤条件
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

  /**
   * 部门表的别名
   * <p>
   * 例如 SQL 为：select * from sys_dept d，则 alias 应为 "d"。<br>
   * 如果 SQL 为单表且无别名，则留空。
   * </p>
   */
  String deptAlias() default "";

  /**
   * 部门字段名
   * <p>
   * 默认为 "dept_id"
   * </p>
   */
  String deptField() default "dept_id";

  /**
   * 用户表的别名
   * <p>
   * 用于“仅本人”权限时，限定 create_by 字段
   * </p>
   */
  String userAlias() default "";

  /**
   * 用户字段名（通常是创建人 ID）
   * <p>
   * 默认为 "created_by"
   * </p>
   */
  String userField() default "created_by";
}