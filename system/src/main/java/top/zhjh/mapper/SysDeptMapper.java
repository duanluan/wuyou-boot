package top.zhjh.mapper;

import org.apache.ibatis.annotations.Param;
import top.csaf.tree.TreeNode;
import top.zhjh.model.entity.SysDept;
import top.zhjh.model.qo.SysDeptListQO;
import top.zhjh.model.qo.SysDeptPageQO;
import top.zhjh.model.qo.SysDeptTreeQO;
import top.zhjh.model.vo.SysDeptPageVO;
import top.zhjh.mybatis.MyBaseMapper;

import java.util.List;

public interface SysDeptMapper extends MyBaseMapper<SysDept> {

  List<SysDeptPageVO> list(SysDeptListQO query);

  List<SysDeptPageVO> page(SysDeptPageQO query);

  List<TreeNode> listTree(SysDeptTreeQO query);

  /**
   * 根据部门ID查询自身及所有子部门ID
   * @param deptId 部门ID
   * @return ID列表
   */
  List<Long> listChildrenIds(@Param("deptId") Long deptId);
}
