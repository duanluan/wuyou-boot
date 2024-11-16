package top.zhjh.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 逗号分隔字符串转 List&lt;Long&gt; 类型处理器
 */
public class CommasToLongListTypeHandler extends BaseTypeHandler<List<Long>> {

  @Override
  public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<Long> longs, JdbcType jdbcType) throws SQLException {
    if (longs != null) {
      preparedStatement.setString(i, longs.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(",")));
    } else {
      preparedStatement.setNull(i, jdbcType.TYPE_CODE);
    }
  }

  @Override
  public List<Long> getNullableResult(ResultSet resultSet, String s) throws SQLException {
    String result = resultSet.getString(s);
    return convertStringToLongList(result);
  }

  @Override
  public List<Long> getNullableResult(ResultSet resultSet, int i) throws SQLException {
    String result = resultSet.getString(i);
    return convertStringToLongList(result);
  }

  @Override
  public List<Long> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
    String result = callableStatement.getString(i);
    return convertStringToLongList(result);
  }

  private List<Long> convertStringToLongList(String input) {
    if (input == null || input.isEmpty()) {
      return null;
    }
    return Arrays.stream(input.split(","))
      .map(Long::valueOf)
      .collect(Collectors.toList());
  }
}
