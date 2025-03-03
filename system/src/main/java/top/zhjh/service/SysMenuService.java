package top.zhjh.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.csaf.coll.CollUtil;
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

  @Cacheable(value = "menuTree", condition = "#query.isNotBlankTypes()",
    key = "#query.types.toString() + '_' + (!T(top.csaf.coll.CollUtil).isEmpty(#query.roleCodes)  ? #query.roleCodes.toString() : T(cn.dev33.satoken.stp.StpUtil).getRoleList().toString())")
  public List<TreeNode> listTree(@NonNull final SysMenuTreeQO query) {
    // 如未指定角色编码
    if (CollUtil.isEmpty(query.getRoleCodes())) {
      // 查询当前用户所属编码
      query.setRoleCodes(StpUtil.getRoleList());
    }
    return TreeUtil.build(sysMenuMapper.listTree(query));
  }
}
