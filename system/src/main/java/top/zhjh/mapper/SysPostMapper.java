package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.zhjh.model.entity.SysPost;
import top.zhjh.model.qo.SysPostListQO;
import top.zhjh.model.qo.SysPostPageQO;
import top.zhjh.model.vo.SysPostPageVO;

import java.util.List;

public interface SysPostMapper extends BaseMapper<SysPost> {

  List<SysPostPageVO> list(SysPostListQO query);

  List<SysPostPageVO> page(SysPostPageQO query);
}
