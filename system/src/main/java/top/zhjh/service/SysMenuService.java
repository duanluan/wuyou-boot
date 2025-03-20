package top.zhjh.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.csaf.tree.TreeNode;
import top.csaf.tree.TreeUtil;
import top.zhjh.enums.CommonStatus;
import top.zhjh.enums.MenuType;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysMenuMapper;
import top.zhjh.model.entity.SysMenu;
import top.zhjh.model.qo.SysMenuTreeTableQO;
import top.zhjh.model.qo.SysMenuUpdateQO;
import top.zhjh.struct.SysMenuStruct;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单 服务实现
 */
@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

  @Resource
  private SysMenuMapper sysMenuMapper;

  /**
   * 列出树
   *
   * @return 树
   */
  @Cacheable(value = "menuTree", keyGenerator = "roleCodesCacheKeyGen")
  public List<TreeNode> listTree() {
    // 树只能查询用户所属的启用的目录 、菜单
    SysMenuTreeTableQO query = new SysMenuTreeTableQO();
    query.setStatus(CommonStatus.ENABLE);
    query.setTypes(Arrays.asList(MenuType.DIR, MenuType.MENU));
    query.setRoleCodes(StpUtil.getRoleList());
    return TreeUtil.build(sysMenuMapper.listTree(query));
  }

  /**
   * 列出树表格
   *
   * @param query 查询参数
   * @return 树
   */
  public List<TreeNode> listTreeTable(@NonNull final SysMenuTreeTableQO query) {
    if (Boolean.TRUE.equals(query.getNotBuildTree())) {
      return sysMenuMapper.listTree(query);
    }
    return TreeUtil.build(sysMenuMapper.listTree(query));
  }

  /**
   * 更新状态
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @CacheEvict(value = "menuTree", keyGenerator = "roleCodesCacheKeyGen")
  public boolean updateStatus(SysMenuUpdateQO obj) {
    if (this.lambdaQuery().eq(SysMenu::getId, obj.getId()).count() == 0) {
      throw new ServiceException("菜单不存在");
    }
    return this.updateById(SysMenuStruct.INSTANCE.to(obj));
  }
}
