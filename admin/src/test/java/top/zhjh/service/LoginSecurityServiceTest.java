package top.zhjh.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import top.zhjh.exception.ServiceException;
import top.zhjh.prop.LoginSecurityConf;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginSecurityServiceTest {

  @Test
  void recordLoginFailureReturnsRemainingAttemptsBeforeLock() {
    // 使用默认配置，验证首次失败时会返回“还可尝试 4 次”。
    LoginSecurityService service = createService(new LoginSecurityConf());
    StringRedisTemplate stringRedisTemplate = getRedisTemplate(service);
    ValueOperations<String, String> valueOperations = getValueOperations(stringRedisTemplate);
    String failKey = "login:fail:1:admin";
    String lockKey = "login:lock:1:admin";

    when(valueOperations.increment(failKey)).thenReturn(1L);

    String message = service.recordLoginFailure("Admin", 1L);

    assertEquals("用户名或密码错误，还可尝试 4 次", message);
    verify(stringRedisTemplate).expire(failKey, Duration.ofMinutes(30));
    verify(valueOperations, never()).set(lockKey, "1", Duration.ofMinutes(15));
  }

  @Test
  void recordLoginFailureLocksImmediatelyWhenThresholdReached() {
    // 第 5 次失败达到阈值后，应立刻进入锁定而不是提示“剩余 0 次”。
    LoginSecurityConf loginSecurityConf = new LoginSecurityConf();
    loginSecurityConf.setLockMinutes(15);
    LoginSecurityService service = createService(loginSecurityConf);
    StringRedisTemplate stringRedisTemplate = getRedisTemplate(service);
    ValueOperations<String, String> valueOperations = getValueOperations(stringRedisTemplate);
    String failKey = "login:fail:1:admin";
    String lockKey = "login:lock:1:admin";

    when(valueOperations.increment(failKey)).thenReturn(5L);

    String message = service.recordLoginFailure("Admin", 1L);

    assertEquals("登录失败次数过多，请 15 分钟后再试", message);
    verify(valueOperations).set(lockKey, "1", Duration.ofMinutes(15));
    verify(stringRedisTemplate).delete(failKey);
  }

  @Test
  void validateNotLockedUsesRemainingLockTimeMessage() {
    // 锁定中的账号应直接拿 Redis TTL 组装剩余锁定时间提示。
    LoginSecurityService service = createService(new LoginSecurityConf());
    StringRedisTemplate stringRedisTemplate = getRedisTemplate(service);
    ValueOperations<String, String> valueOperations = getValueOperations(stringRedisTemplate);
    String lockKey = "login:lock:1:admin";

    when(valueOperations.get(lockKey)).thenReturn("1");
    when(stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS)).thenReturn(92L);

    ServiceException exception = assertThrows(ServiceException.class, () -> service.validateNotLocked("Admin", 1L));

    assertEquals("登录失败次数过多，请 1 分 32 秒后再试", exception.getMessage());
  }

  private LoginSecurityService createService(LoginSecurityConf loginSecurityConf) {
    // 该单测只验证纯业务逻辑，因此通过反射注入配置和 Redis mock，避免启动 Spring 容器。
    LoginSecurityService service = new LoginSecurityService();
    ReflectionTestUtils.setField(service, "loginSecurityConf", loginSecurityConf);
    ReflectionTestUtils.setField(service, "stringRedisTemplate", mock(StringRedisTemplate.class));
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    when(getRedisTemplate(service).opsForValue()).thenReturn(valueOperations);
    return service;
  }

  private StringRedisTemplate getRedisTemplate(LoginSecurityService service) {
    // 取回注入到服务中的 Redis mock，便于在各测试用例中继续打桩和校验交互。
    return (StringRedisTemplate) ReflectionTestUtils.getField(service, "stringRedisTemplate");
  }

  @SuppressWarnings("unchecked")
  private ValueOperations<String, String> getValueOperations(StringRedisTemplate stringRedisTemplate) {
    // 统一封装 ValueOperations 获取，减少重复代码。
    return stringRedisTemplate.opsForValue();
  }
}
