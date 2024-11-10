package top.zhjh.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.csaf.tree.TreeNode;
import top.csaf.tree.TreeUtil;
import top.zhjh.mapper.SysMenuMapper;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.SysMenuTreeQO;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单 服务实现
 */
@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

  @Resource
  private SysMenuMapper sysMenuMapper;

  @Cacheable(value = "menuTree", key = "#query.types", condition = "#query.isNotBlankTypes()")
  public List<TreeNode> listTree(SysMenuTreeQO query) {
    query.setRoleCodeList(StpUtil.getRoleList());
    return TreeUtil.build(sysMenuMapper.listTree(query));
  }
}
