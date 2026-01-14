package top.zhjh.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.csaf.coll.CollUtil;
import top.zhjh.base.model.PageVO;
import top.zhjh.enums.DataScopeActionType;
import top.zhjh.enums.DataScopeType;
import top.zhjh.exception.ServiceException;
import top.zhjh.mapper.SysDeptMapper;
import top.zhjh.mapper.SysTestDataMapper;
import top.zhjh.model.entity.SysRole;
import top.zhjh.model.entity.SysRoleDept;
import top.zhjh.model.entity.SysTestData;
import top.zhjh.model.entity.SysUser;
import top.zhjh.model.qo.SysTestDataPageQO;
import top.zhjh.model.qo.SysTestDataSaveQO;
import top.zhjh.model.qo.SysTestDataUpdateQO;
import top.zhjh.model.vo.SysTestDataPageVO;
import top.zhjh.struct.SysTestDataStruct;
import top.zhjh.util.StpExtUtil;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTestDataService extends ServiceImpl<SysTestDataMapper, SysTestData> {

  @Resource
  private SysTestDataMapper sysTestDataMapper;
  @Resource
  private SysUserService sysUserService;
  @Resource
  private SysRoleService sysRoleService;
  @Resource
  private SysRoleDeptService sysRoleDeptService;
  @Resource
  private SysDeptMapper sysDeptMapper;

  public PageVO<SysTestDataPageVO> page(SysTestDataPageQO query) {
    List<SysTestDataPageVO> records = sysTestDataMapper.page(query);
    return new PageVO<SysTestDataPageVO>(query).setRecords(records);
  }

  public boolean save(SysTestDataSaveQO obj) {
    this.checkDeptDataScope(obj.getDeptId());
    return this.save(SysTestDataStruct.INSTANCE.to(obj));
  }

  public boolean updateById(SysTestDataUpdateQO obj) {
    SysTestData existTestData = sysTestDataMapper.selectById(obj.getId());
    if (existTestData == null) {
      throw new ServiceException("数据不存在或无查询权限");
    }
    int rows = sysTestDataMapper.updateById(SysTestDataStruct.INSTANCE.to(obj));
    if (rows == 0) {
      throw new ServiceException("无增删改权限");
    }
    return true;
  }

  public boolean removeByIds(List<Long> idList) {
    if (CollUtil.isEmpty(idList)) {
      return false;
    }
    int rows = sysTestDataMapper.deleteByIds(idList);
    if (rows == 0) {
      throw new ServiceException("无删除权限或数据不存在");
    }
    return true;
  }

  /**
   * 校验目标部门是否在用户的（增删改）数据权限范围内
   *
   * @param targetDeptId 目标部门ID
   */
  private void checkDeptDataScope(Long targetDeptId) {
    if (targetDeptId == null) {
      return;
    }
    if (StpExtUtil.isSuperAdmin()) {
      return;
    }

    // 获取当前用户和角色
    Long userId = StpUtil.getLoginIdAsLong();
    SysUser user = sysUserService.getById(userId);
    if (user == null || CollUtil.isEmpty(user.getRoleIds())) {
      throw new ServiceException("无权操作：用户无角色");
    }
    List<SysRole> roles = sysRoleService.listByIds(user.getRoleIds());

    // 计算用户的（增删改）数据权限范围
    Set<Long> allowedDeptIds = new HashSet<>();
    boolean isAll = false;

    // 预查询自定义权限的部门 (ActionType = UPDATE)
    List<Long> customScopeRoleIds = roles.stream()
      .filter(r -> DataScopeType.CUSTOM.equals(r.getUpdateDataScope()))
      .map(SysRole::getId)
      .collect(Collectors.toList());

    List<SysRoleDept> customRoleDepts = null;
    if (CollUtil.isNotEmpty(customScopeRoleIds)) {
      customRoleDepts = sysRoleDeptService.lambdaQuery()
        .select(SysRoleDept::getRoleId, SysRoleDept::getDeptId, SysRoleDept::getDataScopeActionType)
        .in(SysRoleDept::getRoleId, customScopeRoleIds)
        .eq(SysRoleDept::getDataScopeActionType, DataScopeActionType.UPDATE)
        .list();
    }

    for (SysRole role : roles) {
      DataScopeType scopeType = role.getUpdateDataScope();
      if (scopeType == null) continue;

      switch (scopeType) {
        case ALL -> isAll = true;
        case CUSTOM -> {
          if (customRoleDepts != null) {
            customRoleDepts.stream()
              .filter(rd -> rd.getRoleId().equals(role.getId()))
              .forEach(rd -> allowedDeptIds.add(rd.getDeptId()));
          }
        }
        case CURRENT_DEPT_AND_CHILDREN -> {
          if (CollUtil.isNotEmpty(user.getDeptIds())) {
            allowedDeptIds.addAll(user.getDeptIds());
            // 遍历所有部门查子部门
            for (Long deptId : user.getDeptIds()) {
              List<Long> childIds = sysDeptMapper.listChildrenIds(deptId);
              if (CollUtil.isNotEmpty(childIds)) {
                allowedDeptIds.addAll(childIds);
              }
            }
          }
        }
        case CURRENT_DEPT -> {
          if (CollUtil.isNotEmpty(user.getDeptIds())) {
            allowedDeptIds.addAll(user.getDeptIds());
          }
        }
        case ONLY_SELF -> {
          // 仅本人：允许在自己所属的任意部门创建
          if (CollUtil.isNotEmpty(user.getDeptIds())) {
            allowedDeptIds.addAll(user.getDeptIds());
          }
        }
      }
      if (isAll) break;
    }

    if (!isAll && !allowedDeptIds.contains(targetDeptId)) {
      throw new ServiceException("无权在所选部门创建数据");
    }
  }}