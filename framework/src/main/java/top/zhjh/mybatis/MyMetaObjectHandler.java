package top.zhjh.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import top.csaf.bean.BeanUtil;
import top.zhjh.base.model.BaseEntity;

/**
 * MyBatis Plus 自动填充
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    if (StpUtil.isLogin()) {
      // 如果有登录用户，设置创建人
      this.strictInsertFill(metaObject, BeanUtil.getPropertyName(BaseEntity::getCreatedBy), StpUtil::getLoginIdAsLong, Long.class);
    }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    if (StpUtil.isLogin()) {
      // 如果有登录用户，设置更新人
      this.strictUpdateFill(metaObject, BeanUtil.getPropertyName(BaseEntity::getUpdatedBy), StpUtil::getLoginIdAsLong, Long.class);
    }
  }
}
