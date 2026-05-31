package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.zhjh.model.entity.SysDictItem;
import top.zhjh.model.qo.SysDictItemListQO;
import top.zhjh.model.qo.SysDictItemPageQO;
import top.zhjh.model.vo.SysDictItemPageVO;

import java.util.List;

public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

  List<SysDictItemPageVO> list(SysDictItemListQO query);

  List<SysDictItemPageVO> page(SysDictItemPageQO query);
}
