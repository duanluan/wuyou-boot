package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.csaf.tree.TreeNode;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.SysMenuTreeTableQO;

import java.util.List;

/**
 * 菜单 Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

  List<TreeNode> listTree(SysMenuTreeTableQO query);
}
