package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhjh.mapper.SysRoleMapper;
import top.zhjh.model.entity.SysRole;

/**
 * 角色 服务实现
 *
  * @author ZhongJianhao
 */
@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

  @Autowired
  private SysRoleMapper sysRoleMapper;

}
