package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.zhjh.mapper.SysDeptUserMapper;
import top.zhjh.model.entity.SysDeptUser;

import javax.annotation.Resource;

/**
 * 部门-用户 服务实现
 */
@Service
public class SysDeptUserService extends ServiceImpl<SysDeptUserMapper, SysDeptUser> {

  @Resource
  private SysDeptUserMapper sysDeptUserMapper;
}
