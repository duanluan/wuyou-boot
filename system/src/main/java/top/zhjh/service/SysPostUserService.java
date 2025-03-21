package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.zhjh.mapper.SysPostUserMapper;
import top.zhjh.model.entity.SysPostUser;

import javax.annotation.Resource;

/**
 * 岗位-用户 服务实现
 */
@Service
public class SysPostUserService extends ServiceImpl<SysPostUserMapper, SysPostUser> {

  @Resource
  private SysPostUserMapper sysPostUserMapper;
}
