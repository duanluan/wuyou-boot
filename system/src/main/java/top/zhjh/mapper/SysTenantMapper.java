package top.zhjh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.zhjh.base.model.PageVO;
import top.zhjh.model.entity.SysTenant;
import top.zhjh.model.qo.SysTenantListQO;
import top.zhjh.model.qo.SysTenantPageQO;
import top.zhjh.model.vo.SysTenantPageVO;

import java.util.List;

public interface SysTenantMapper extends BaseMapper<SysTenant> {

  List<SysTenantPageVO> list(SysTenantListQO query);

  PageVO<SysTenantPageVO> page(SysTenantPageQO query);
}
