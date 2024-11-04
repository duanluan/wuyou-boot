package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.csaf.lang.StrUtil;
import top.zhjh.enums.RoleEnum;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysRoleMapper;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.entity.SysRoleUser;
import top.zhjh.model.qo.SysRoleRemoveQO;
import top.zhjh.model.qo.SysRoleUpdateQO;

/**
 * 角色 服务实现
 *
 * @author ZhongJianhao
 */
@Slf4j
@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

  @Autowired
  private SysRoleMapper sysRoleMapper;
  @Autowired
  private SysRoleUserService sysRoleUserService;

  public boolean remove(SysRoleRemoveQO query) {
    String idsStr = query.getIds();
    if (StrUtil.isBlank(idsStr)) {
      return false;
    }
    String[] ids = idsStr.split(",");
    for (String id : ids) {
      SysRole role = this.getById(id);
      if (role == null) {
        log.error("角色不存在: {}", id);
        throw new ServiceException("角色不存在");
      }
      if (RoleEnum.SUPER_ADMIN.getCode().equals(role.getCode())) {
        throw new ServiceException("不能删除编码为" + RoleEnum.SUPER_ADMIN.getCode() + "的角色");
      }
      if (sysRoleUserService.lambdaQuery().eq(SysRoleUser::getRoleId, id).count() > 0) {
        throw new ServiceException("不能删除关联了用户的角色");
      }
    }
    return this.removeBatchByIds(Arrays.asList(ids));
  }
}
