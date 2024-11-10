package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.zhjh.mapper.SysRoleUserMapper;
import top.zhjh.model.entity.SysRoleUser;

import javax.annotation.Resource;

/**
 * 角色-用户 服务实现
 */
@Service
public class SysRoleUserService extends ServiceImpl<SysRoleUserMapper, SysRoleUser> {

  @Resource
  private SysRoleUserMapper sysRoleUserMapper;
}
