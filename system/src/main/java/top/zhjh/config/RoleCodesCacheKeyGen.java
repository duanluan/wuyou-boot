package top.zhjh.config;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import top.csaf.coll.CollUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 缓存 Key 生成器：角色
 */
@Component
public class RoleCodesCacheKeyGen implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    List<String> roleList = StpUtil.getRoleList();
    if (CollUtil.isEmpty(roleList)) {
      return method.getName();
    }
    return roleList.toString();
  }
}
