package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.csaf.tree.TreeNode;
import top.csaf.tree.TreeUtil;
import top.zhjh.mapper.SysMenuMapper;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.SysMenuTreeQO;

import java.util.List;

/**
 * 菜单 服务实现
 *
  * @author ZhongJianhao
 */
@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

  @Autowired
  private SysMenuMapper sysMenuMapper;

  public List<TreeNode> listTree(SysMenuTreeQO query) {
    return TreeUtil.build(sysMenuMapper.listTree(query));
  }
}
