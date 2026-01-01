package top.zhjh.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.csaf.lang.StrUtil;
import top.csaf.tree.TreeConfig;
import top.csaf.tree.TreeNode;
import top.csaf.tree.TreeUtil;
import top.zhjh.base.model.PageVO;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDeptMapper;
import top.zhjh.model.entity.SysDept;
import top.zhjh.model.qo.*;
import top.zhjh.model.vo.SysDeptPageVO;
import top.zhjh.mybatis.MyServiceImpl;
import top.zhjh.struct.SysDeptStruct;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysDeptService extends MyServiceImpl<SysDeptMapper, SysDept> {

  @Resource
  private SysDeptMapper sysDeptMapper;

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysDeptPageVO> list(SysDeptListQO query) {
    return sysDeptMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public PageVO<SysDeptPageVO> page(SysDeptPageQO query) {
    List<SysDeptPageVO> records = sysDeptMapper.page(query);
    return new PageVO<SysDeptPageVO>(query).setRecords(records);
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
    return TreeUtil.build(sysDeptMapper.listTree(query), TreeConfig.builder().idType(String.class).build());
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysDeptSaveQO obj) {
    // 1. 校验同级部门名称是否重复
    if (this.lambdaQuery()
      .eq(SysDept::getParentId, obj.getParentId())
      .eq(SysDept::getName, obj.getName())
      .count() > 0) {
      throw new ServiceException("同级部门名称不能重复");
    }

    // 2. 转换实体
    SysDept sysDept = SysDeptStruct.INSTANCE.to(obj);

    // 3. 第一次保存：为了获取数据库生成的 ID
    boolean saveResult = this.save(sysDept);
    if (!saveResult || sysDept.getId() == null) {
      return false;
    }

    // 4. 计算组级列表 (规则：顶级ID,……,父级ID,自身ID)
    String ancestors = calculateAncestors(sysDept.getParentId(), sysDept.getId());

    // 5. 更新自身的 ancestors
    return this.lambdaUpdate()
      .set(SysDept::getAncestors, ancestors)
      .eq(SysDept::getId, sysDept.getId())
      .update();
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysDeptUpdateQO obj) {
    // 1. 基础校验：存在性
    SysDept oldDept = this.getById(obj.getId());
    if (oldDept == null) {
      throw new ServiceException("部门不存在");
    }

    // 2. 基础校验：同级名称重复 (排除自身)
    if (this.lambdaQuery()
      .eq(SysDept::getParentId, obj.getParentId())
      .eq(SysDept::getName, obj.getName())
      .ne(SysDept::getId, obj.getId())
      .count() > 0) {
      throw new ServiceException("同级部门名称不能重复");
    }

    // 3. 转换实体
    SysDept newDept = SysDeptStruct.INSTANCE.to(obj);

    // 4. 判断是否修改了父级 ID 或者 有父级 ID 但是旧数据没有 ancestors（用作修复）
    if (!oldDept.getParentId().equals(newDept.getParentId()) || StrUtil.isBlank(oldDept.getAncestors())) {
      // 4.1 防御性校验：不能将父级设置为自己
      if (newDept.getId().equals(newDept.getParentId())) {
        throw new ServiceException("父级部门不能是自己");
      }

      // 4.2 计算自身新的组级列表
      String newAncestors = calculateAncestors(newDept.getParentId(), newDept.getId());
      String oldAncestors = oldDept.getAncestors();

      // 4.3 级联更新子孙节点
      // 查找所有子孙节点：ancestors like 'oldAncestors,%'
      // 注意：这里加逗号是为了精确匹配，防止 ID 前缀重叠（如 1,100 被 1,1001 匹配）
      List<SysDept> children = this.lambdaQuery()
        .likeRight(SysDept::getAncestors, oldAncestors + ",")
        .list();

      if (!children.isEmpty()) {
        for (SysDept child : children) {
          // 将子孙节点路径中的旧前缀替换为新前缀
          // 例：旧(1,100), 新(1,200,100), 子(1,100,55) -> (1,200,100,55)
          String targetAncestors = child.getAncestors().replaceFirst(oldAncestors, newAncestors);
          child.setAncestors(targetAncestors);
        }
        // 批量更新子孙节点
        this.updateBatchById(children);
      }

      // 设置自身的新 ancestors
      newDept.setAncestors(newAncestors);
    } else {
      // 父级未变，保持原有 ancestors 避免被 mapper 覆盖为空（如果 to 方法不处理的话）
      newDept.setAncestors(oldDept.getAncestors());
    }

    // 5. 执行更新
    return this.updateById(newDept);
  }

  /**
   * 内部通用方法：计算组级列表
   *
   * @param parentId 父级 ID
   * @param selfId   自身 ID
   * @return 完整的 ancestors 字符串
   */
  private String calculateAncestors(Long parentId, Long selfId) {
    if (parentId == null || parentId == 0L) {
      // 顶级节点 -> 自身 ID
      return String.valueOf(selfId);
    }

    SysDept parent = this.getById(parentId);
    if (parent == null) {
      throw new ServiceException("父级部门不存在");
    }
    if (parent.getAncestors() == null || parent.getAncestors().isEmpty()) {
      throw new ServiceException("父级部门数据异常：缺少组级列表，请联系管理员修复");
    }

    // 子节点 -> 父级 ancestors + "," + 自身 ID
    return parent.getAncestors() + "," + selfId;
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
