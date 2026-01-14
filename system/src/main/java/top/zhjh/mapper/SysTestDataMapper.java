package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.zhjh.config.datascope.DataScope;
import top.zhjh.model.entity.SysTestData;
import top.zhjh.model.qo.SysTestDataPageQO;
import top.zhjh.model.vo.SysTestDataPageVO;

import java.util.Collection;
import java.util.List;

public interface SysTestDataMapper extends BaseMapper<SysTestData> {

  /**
   * 分页查询
   * 核心测试点：添加 @DataScope 注解
   * deptAlias="t": 对应 XML 中表的别名 t
   * userAlias="t": 对应 XML 中表的别名 t (因为 created_by 也在主表)
   */
  @DataScope(deptAlias = "t", userAlias = "t")
  List<SysTestDataPageVO> page(@Param("query") SysTestDataPageQO query);

  /**
   * 重写 updateById，添加数据权限注解
   * update 语句没有别名，使用默认配置 (deptAlias="", userAlias="")
   */
  @Override
  @DataScope
  int updateById(@Param("et") SysTestData entity);

  /**
   * 重写 deleteByIds (注意：方法名必须是 deleteByIds，且参数必须是 Collection<?>)
   * <p>
   * 1. MyBatis-Plus 3.5+ 中 removeByIds 底层调用的 SQL 方法名是 deleteByIds。
   * 2. 必须显式声明此方法，Interceptor 才能通过反射读取到 @DataScope 注解。
   * 3. 覆盖为抽象方法后，会跳过 BaseMapper default 方法中的逻辑（如 fill），直接执行 SQL。
   * </p>
   */
  @Override
  @DataScope
  int deleteByIds(@Param("coll") Collection<?> idList);
}