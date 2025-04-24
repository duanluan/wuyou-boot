package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.zhjh.mapper.SysTenantUserMapper;
import top.zhjh.model.entity.SysTenantUser;

import javax.annotation.Resource;

/**
 * 租户-用户 服务实现
 */
@Service
public class SysTenantUserService extends ServiceImpl<SysTenantUserMapper, SysTenantUser> {

  @Resource
  private SysTenantUserMapper sysTenantUserMapper;
}
