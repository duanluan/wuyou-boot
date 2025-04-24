package top.zhjh.config.mybatis.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;
import org.springframework.stereotype.Component;
import top.csaf.bean.BeanUtil;
import top.csaf.coll.CollUtil;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.prop.TenantConf;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * MyBatis-Plus 租户处理器：<a href="https://baomidou.com/plugins/tenant/#%E6%AD%A5%E9%AA%A4-1%E5%AE%9E%E7%8E%B0%E7%A7%9F%E6%88%B7%E5%A4%84%E7%90%86%E5%99%A8">步骤 1：实现租户处理器</a>
 */
@Slf4j
@Component
public class MyTenantHandler implements TenantLineHandler {

  @Resource
  private TenantConf tenantConf;

  @Override
  public Expression getTenantId() {
    // 当前用户租户 ID
    Long tenantId = StpExtUtil.getTenantId();
    if (tenantId != null) {
      return new LongValue(tenantId);
    }
    return null;
  }

  @Override
  public String getTenantIdColumn() {
    // 租户 ID 字段名
    return BeanUtil.getColumnName(BaseEntity::getTenantId);
  }

  /**
   * 是否忽略租户
   *
   * @param tableName 表名
   * @return 是否忽略租户
   */
  @Override
  public boolean ignoreTable(String tableName) {
    if ("sys_role".equals(tableName)) {
      log.info("是否忽略特定表：{}，是否禁用租户：{}，是否为超级管理员：{}", CollUtil.contains(tenantConf.getIgnoreTables(), tableName), !StpExtUtil.isEnableTenant(), StpExtUtil.isSuperAdmin());
    }
    // 忽略特定表
    if (CollUtil.contains(tenantConf.getIgnoreTables(), tableName)) {
      return true;
    }
    // 当前线程是否禁用租户
    if (!StpExtUtil.isEnableTenant()) {
      return true;
    }
    // 忽略超级管理员
    return StpExtUtil.isSuperAdmin();
  }

  @Override
  public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
    // 需要忽略的插入字段
    return TenantLineHandler.super.ignoreInsert(columns, tenantIdColumn);
  }
}
