package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.tree.TreeNode;
import top.csaf.tree.TreeUtil;
import top.zhjh.base.model.MyPage;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDeptMapper;
import top.zhjh.model.entity.SysDept;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysDeptPageVO;
import top.zhjh.struct.SysDeptStruct;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysDeptService extends ServiceImpl<SysDeptMapper, SysDept> {

  @Resource
  private SysDeptMapper sysDeptMapper;

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysDeptPageVO> list(SysDeptPageQO query) {
    return sysDeptMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public MyPage<SysDeptPageVO> page(SysDeptPageQO query) {
    return sysDeptMapper.page(query);
  }

  /**
   * 列出树
   *
   * @param query 查询参数
   * @return 树
   */
  public List<TreeNode> listTree(SysDeptTreeQO query) {
    if (Boolean.TRUE.equals(query.getNotBuildTree())) {
      return sysDeptMapper.listTree(query);
    }
    return TreeUtil.build(sysDeptMapper.listTree(query));
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysDeptSaveQO obj) {
    if (this.lambdaQuery()
      .eq(SysDept::getParentId, obj.getParentId())
      .eq(SysDept::getName, obj.getName()).count() > 0) {
      throw new ServiceException("同级部门名称不能重复");
    }
    return this.save(SysDeptStruct.INSTANCE.to(obj));
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysDeptUpdateQO obj) {
    if (this.lambdaQuery().eq(SysDept::getId, obj.getId()).count() == 0) {
      throw new ServiceException("部门不存在");
    }
    if (this.lambdaQuery()
      .eq(SysDept::getParentId, obj.getParentId())
      .eq(SysDept::getName, obj.getName())
      .ne(SysDept::getId, obj.getId()).count() > 0) {
      throw new ServiceException("同级部门名称不能重复");
    }
    return this.updateById(SysDeptStruct.INSTANCE.to(obj));
  }

  /**
   * 更新状态
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  public boolean updateStatus(SysDeptUpdateQO obj) {
    if (this.lambdaQuery().eq(SysDept::getId, obj.getId()).count() == 0) {
      throw new ServiceException("部门不存在");
    }
    return this.updateById(SysDeptStruct.INSTANCE.to(obj));
  }

  /**
   * 删除
   *
   * @param query 删除参数
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysDeptRemoveQO query) {
    if (this.lambdaQuery().in(SysDept::getId, query.getIds()).count() != query.getIds().size()) {
      throw new ServiceException("部门不存在");
    }
    return this.removeByIds(query.getIds());
  }
}
