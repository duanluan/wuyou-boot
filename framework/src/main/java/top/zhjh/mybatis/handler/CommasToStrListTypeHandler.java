package top.zhjh.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 逗号分隔字符串转 List&lt;String&gt; 类型处理器
 */
public class CommasToStrListTypeHandler extends BaseTypeHandler<List<String>> {

  @Override
  public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<String> strs, JdbcType jdbcType) throws SQLException {
    if (strs != null) {
      preparedStatement.setString(i, String.join(",", strs));
    } else {
      preparedStatement.setNull(i, jdbcType.TYPE_CODE);
    }
  }

  @Override
  public List<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
    String result = resultSet.getString(s);
    return convertStringToList(result);
  }

  @Override
  public List<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
    String result = resultSet.getString(i);
    return convertStringToList(result);
  }

  @Override
  public List<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
    String result = callableStatement.getString(i);
    return convertStringToList(result);
  }

  private List<String> convertStringToList(String input) {
    if (input == null || input.isEmpty()) {
      return null;
    }
    return Arrays.asList(input.split(","));
  }
}
