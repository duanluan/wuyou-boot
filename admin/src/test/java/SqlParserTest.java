import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import top.csaf.coll.CollUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.Application;
import top.zhjh.util.JSqlParserUtil;

import java.util.List;

@ActiveProfiles("local")
@SpringBootTest(classes = Application.class)
public class SqlParserTest {

  @Test
  public void testSelect() throws JSQLParserException {
    String sql = "select" +
      "   sm.id, sm.created_time createdTime, sm.parent_id parentId, sm.type, sm.name, sm.sort, sm.status, sm.icon, sm.path, sm.method, sm.permission, " +
      "   (select count(*) from sys_role)" +
      " from sys_menu sm" +
      " left join (select 1 from sys_user) su on (select 1 from sys_menu limit 1) and (select 1 from sys_user limit 1) != 1" +
      " where sm.deleted = 0 and sm.status = 1" +
      "   and (select 1 from sys_menu limit 1)" +
      "   and sm.id in ( " +
      "     select menu_id from sys_role_menu where role_id in ( select id from sys_role where code in ( 'tenantAdmin' ) " +
      "   ) ) and ( sm.type = 1 or sm.type = 2 )" +
      " order by sort, created_time desc";
    Select select = (Select) CCJSqlParserUtil.parse(sql);
    this.handlePlainSelect(select.getPlainSelect());
    System.out.println(select);
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

    this.handleJoins(plainSelect.getJoins());

    // where 中的子查询
    this.handleWhere(plainSelect.getWhere());

    // 如果有表
    FromItem fromItem = plainSelect.getFromItem();
    if (fromItem instanceof Table table) {

      // where 加入租户条件
      Expression where = plainSelect.getWhere();

      // 创建 tenant_id = 1 条件
      EqualsTo tenantCondition = new EqualsTo();
      String columnName = "tenant_id";
      if (StrUtil.isNotBlank(table.getAlias())) {
        columnName = table.getAlias() + "." + columnName;
      }
      tenantCondition.setLeftExpression(new Column(columnName));
      tenantCondition.setRightExpression(new LongValue(1));

      // 将新条件添加到现有条件中
      if (where != null) {
        // 如果已有条件，使用 AND 连接
        AndExpression newCondition = new AndExpression(where, tenantCondition);
        plainSelect.setWhere(newCondition);
      } else {
        // 如果没有现有条件，直接设置
        plainSelect.setWhere(tenantCondition);
      }
    }
  }

  private void handleJoins(List<Join> joins) {
    if (CollUtil.isEmpty(joins)) {
      return;
    }
    for (Join join : joins) {
      FromItem fromItem = join.getFromItem();
      // join 中的子查询
      if (fromItem instanceof ParenthesedSelect) {
        this.handlePlainSelect(this.getPlainSelect((ParenthesedSelect) fromItem));
      }
      // join 中 on 的子查询
      for (Expression on : join.getOnExpressions()) {
        this.handleWhere(on);
      }
    }
  }

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

  @Test
  public void testInsert() throws JSQLParserException {
    String sql = "insert into sys_menu (id) " +
      "values ((select 1 from sys_user where 1=2 limit 1 ))";
    Insert insert = (Insert) CCJSqlParserUtil.parse(sql);
    for (Expression expression : insert.getValues().getExpressions()) {
      if (expression instanceof ParenthesedSelect) {
        this.handlePlainSelect(this.getPlainSelect((ParenthesedSelect) expression));
      }
    }
    System.out.println(insert);
  }
}
