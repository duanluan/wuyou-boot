package top.zhjh.mapper;

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
}
