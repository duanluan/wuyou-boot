package top.zhjh.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;

public interface MyBaseMapper<T> extends BaseMapper<T> {
  /**
   * 根据 ID 查询总数
   *
   * @param id 主键ID
   */
  long countById(Serializable id);
}
