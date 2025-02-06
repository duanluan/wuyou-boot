package top.zhjh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zhjh.base.model.MyPage;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysPostMapper;
import top.zhjh.model.qo.SysPostListQO;
import top.zhjh.model.entity.SysPost;
import top.zhjh.model.qo.SysPostPageQO;
import top.zhjh.model.qo.SysPostRemoveQO;
import top.zhjh.model.qo.SysPostSaveQO;
import top.zhjh.model.qo.SysPostUpdateQO;
import top.zhjh.model.vo.SysPostPageVO;
import top.zhjh.struct.SysPostStruct;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysPostService extends ServiceImpl<SysPostMapper, SysPost> {

  @Resource
  private SysPostMapper sysPostMapper;

  /**
   * 列出
   *
   * @param query 查询参数
   * @return 列表
   */
  public List<SysPostPageVO> list(SysPostListQO query) {
    return sysPostMapper.list(query);
  }

  /**
   * 分页
   *
   * @param query 查询参数
   * @return 分页列表
   */
  public MyPage<SysPostPageVO> page(SysPostPageQO query) {
    return sysPostMapper.page(query);
  }

  /**
   * 保存
   *
   * @param obj 保存入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean save(SysPostSaveQO obj) {
    if (this.lambdaQuery()
      .and(c -> c
        .eq(SysPost::getName, obj.getName())
        .or()
        .eq(SysPost::getCode, obj.getCode())
      ).count() > 0) {
      throw new ServiceException("名称或编码不能重复");
    }
    return this.save(SysPostStruct.INSTANCE.to(obj));
  }

  /**
   * 更新
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean update(SysPostUpdateQO obj) {
    if (this.lambdaQuery().eq(SysPost::getId, obj.getId()).count() == 0) {
      throw new ServiceException("岗位不存在");
    }
    if (this.lambdaQuery()
      .and(c -> c
        .eq(SysPost::getName, obj.getName())
        .or()
        .eq(SysPost::getCode, obj.getCode())
      )
      .ne(SysPost::getId, obj.getId()).count() > 0) {
      throw new ServiceException("名称或编码不能重复");
    }
    return this.updateById(SysPostStruct.INSTANCE.to(obj));
  }

  /**
   * 更新状态
   *
   * @param obj 更新入参
   * @return 是否成功
   */
  public boolean updateStatus(SysPostUpdateQO obj) {
    if (this.lambdaQuery().eq(SysPost::getId, obj.getId()).count() == 0) {
      throw new ServiceException("岗位不存在");
    }
    return this.updateById(SysPostStruct.INSTANCE.to(obj));
  }

  /**
   * 删除
   *
   * @param query 删除参数
   * @return 是否成功
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public boolean remove(SysPostRemoveQO query) {
    if (this.lambdaQuery().in(SysPost::getId, query.getIds()).count() != query.getIds().size()) {
      throw new ServiceException("岗位不存在");
    }
    return this.removeByIds(query.getIds());
  }
}
