package top.zhjh.mybatis;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public class MyServiceImpl<M extends MyBaseMapper<T>, T> extends ServiceImpl<M, T> implements IService<T> {

  public long countById(Long id) {
    return baseMapper.countById(id);
  }
}
