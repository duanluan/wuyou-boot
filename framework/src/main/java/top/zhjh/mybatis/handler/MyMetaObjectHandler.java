package top.zhjh.mybatis.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import top.csaf.bean.BeanUtil;
import top.zhjh.base.model.BaseEntity;
import top.zhjh.service.SecurityContext;

import javax.annotation.Resource;

/**
 * MyBatis Plus 自动填充
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  // 注入接口，使用 @Lazy 防止在某些极端情况下循环依赖（虽然这里是跨模块，主要是为了稳妥）
  // 运行时 Spring 会找到 system 模块里的实现类并注入进来
  @Lazy
  @Resource
  private SecurityContext securityContext;

  @Override
  public void insertFill(MetaObject metaObject) {
    // 1. 自动填充创建人
    if (StpUtil.isLogin()) {
      this.strictInsertFill(metaObject, BeanUtil.getPropertyName(BaseEntity::getCreatedBy), StpUtil::getLoginIdAsLong, Long.class);
    }

    // 2. 自动填充租户 ID
    if (metaObject.hasSetter("tenantId")) {
      try {
        Long currentTenantId = securityContext.getTenantId();
        boolean isSuperAdmin = StpUtil.isLogin() && securityContext.isSuperAdmin();

        if (isSuperAdmin) {
          // 场景A：超级管理员
          Object originalVal = getFieldValByName("tenantId", metaObject);
          if (originalVal == null) {
            this.strictInsertFill(metaObject, "tenantId", Long.class, currentTenantId);
          }
        } else {
          // 场景B：普通租户管理员/普通用户
          this.setFieldValByName("tenantId", currentTenantId, metaObject);
        }
      } catch (Exception e) {
        // 某些情况下（如未登录、Bean未初始化）可能会报错，建议吞掉异常或打印警告
        log.warn("MyMetaObjectHandler 自动填充 tenantId 失败: {}", e.getMessage());
      }
    }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    // 自动填充更新人
    if (StpUtil.isLogin()) {
      this.strictUpdateFill(metaObject, BeanUtil.getPropertyName(BaseEntity::getUpdatedBy), StpUtil::getLoginIdAsLong, Long.class);
    }
  }
}