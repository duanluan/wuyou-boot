package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.zhjh.model.entity.SysDict;
import top.zhjh.model.qo.SysDictListQO;
import top.zhjh.model.qo.SysDictPageQO;
import top.zhjh.model.vo.SysDictPageVO;

import java.util.List;

public interface SysDictMapper extends BaseMapper<SysDict> {

  List<SysDictPageVO> list(SysDictListQO query);

  List<SysDictPageVO> page(SysDictPageQO query);
}
