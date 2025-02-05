package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zhjh.base.model.MyPage;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysTenantMapper;
import top.zhjh.model.entity.SysTenant;
import top.zhjh.model.qo.SysTenantPageQO;
import top.zhjh.model.qo.SysTenantRemoveQO;
import top.zhjh.model.qo.SysTenantSaveQO;
import top.zhjh.model.qo.SysTenantUpdateQO;
import top.zhjh.model.vo.SysTenantPageVO;
import top.zhjh.struct.SysTenantStruct;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysTenantService extends ServiceImpl<SysTenantMapper, SysTenant> {

  @Resource
  private SysTenantMapper sysTenantMapper;

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysTenantPageVO> list(SysTenantPageQO query) {
    return sysTenantMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public MyPage<SysTenantPageVO> page(SysTenantPageQO query) {
    return sysTenantMapper.page(query);
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysTenantSaveQO obj) {
    if (this.lambdaQuery()
      .eq(SysTenant::getName, obj.getName()).count() > 0) {
      throw new ServiceException("同级部门名称不能重复");
    }
    return this.save(SysTenantStruct.INSTANCE.to(obj));
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysTenantUpdateQO obj) {
    if (this.lambdaQuery().eq(SysTenant::getId, obj.getId()).count() == 0) {
      throw new ServiceException("部门不存在");
    }
    if (this.lambdaQuery()
      .eq(SysTenant::getName, obj.getName())
      .ne(SysTenant::getId, obj.getId()).count() > 0) {
      throw new ServiceException("同级部门名称不能重复");
    }
    return this.updateById(SysTenantStruct.INSTANCE.to(obj));
  }

  /**
   * 更新状态
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  public boolean updateStatus(SysTenantUpdateQO obj) {
    if (this.lambdaQuery().eq(SysTenant::getId, obj.getId()).count() == 0) {
      throw new ServiceException("部门不存在");
    }
    return this.updateById(SysTenantStruct.INSTANCE.to(obj));
  }

  /**
   * 删除
   *
   * @param query 删除参数
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysTenantRemoveQO query) {
    if (this.lambdaQuery().in(SysTenant::getId, query.getIds()).count() != query.getIds().size()) {
      throw new ServiceException("部门不存在");
    }
    return this.removeByIds(query.getIds());
  }
}
