package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.csaf.tree.TreeNode;
import top.zhjh.base.model.MyPage;
import top.zhjh.model.entity.SysDept;
import top.zhjh.model.qo.SysDeptPageQO;
import top.zhjh.model.qo.SysDeptTreeQO;
import top.zhjh.model.vo.SysDeptPageVO;

import java.util.List;

public interface SysDeptMapper extends BaseMapper<SysDept> {

  List<SysDeptPageVO> list(SysDeptPageQO query);

  MyPage<SysDeptPageVO> page(SysDeptPageQO query);

  List<TreeNode> listTree(SysDeptTreeQO query);

  List<TreeNode> listTreeSelect(SysDeptTreeQO query);
}