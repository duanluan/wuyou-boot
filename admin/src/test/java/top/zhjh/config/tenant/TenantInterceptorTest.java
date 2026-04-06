package top.zhjh.config.tenant;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import top.zhjh.prop.TenantConf;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantInterceptorTest {

  @Test
  void applyTenantWhereAddsMainTableConditionForUpdate() throws Exception {
    TenantInterceptor interceptor = new TenantInterceptor();
    ReflectionTestUtils.setField(interceptor, "tenantConf", tenantConf());
    Update update = (Update) CCJSqlParserUtil.parse("UPDATE sys_test_data t SET value = 'x' WHERE id = 1");

    interceptor.applyTenantWhere(update, 9L);

    assertTrue(update.toString().contains("t.tenant_id = 9"));
  }

  @Test
  void applyTenantWhereAddsMainTableConditionForDelete() throws Exception {
    TenantInterceptor interceptor = new TenantInterceptor();
    ReflectionTestUtils.setField(interceptor, "tenantConf", tenantConf());
    Delete delete = (Delete) CCJSqlParserUtil.parse("DELETE FROM sys_test_data t WHERE id = 1");

    interceptor.applyTenantWhere(delete, 9L);

    assertTrue(delete.toString().contains("t.tenant_id = 9"));
  }

  @Test
  void applyTenantWhereSkipsIgnoredTables() throws Exception {
    TenantInterceptor interceptor = new TenantInterceptor();
    ReflectionTestUtils.setField(interceptor, "tenantConf", tenantConf());
    Delete delete = (Delete) CCJSqlParserUtil.parse("DELETE FROM sys_user WHERE id = 1");

    interceptor.applyTenantWhere(delete, 9L);

    assertFalse(delete.toString().contains("tenant_id"));
  }

  private TenantConf tenantConf() {
    TenantConf tenantConf = new TenantConf();
    tenantConf.setTenantIdColumn("tenant_id");
    tenantConf.setIgnoreTables(List.of("sys_user"));
    return tenantConf;
  }
}
