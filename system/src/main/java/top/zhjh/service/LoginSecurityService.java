package top.zhjh.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.csaf.lang.StrUtil;
import top.zhjh.exception.ServiceException;
import top.zhjh.model.vo.CaptchaVO;
import top.zhjh.prop.LoginSecurityConf;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginSecurityService {

  // 验证码答案存储前缀，实际 key 形如 login:captcha:{captchaId}。
  private static final String CAPTCHA_KEY_PREFIX = "login:captcha:";
  // 登录失败计数前缀，实际 key 形如 login:fail:{tenantId}:{username}。
  private static final String FAIL_KEY_PREFIX = "login:fail:";
  // 登录锁定标记前缀，实际 key 形如 login:lock:{tenantId}:{username}。
  private static final String LOCK_KEY_PREFIX = "login:lock:";
  // 用户名或密码错误时的统一提示前缀。
  private static final String LOGIN_FAIL_MSG_PREFIX = "用户名或密码错误";
  // 账号已锁定时的统一提示前缀。
  private static final String LOGIN_LOCK_MSG_PREFIX = "登录失败次数过多，请 ";

  @Resource
  // 登录安全相关配置项，集中控制验证码、锁定与密码强度策略。
  private LoginSecurityConf loginSecurityConf;
  @Resource
  // 基于字符串值读写 Redis，适合验证码、失败次数和锁定标记这种轻量状态。
  private StringRedisTemplate stringRedisTemplate;

  // 生成验证码时使用安全随机数，避免可预测。
  private final SecureRandom secureRandom = new SecureRandom();

  public CaptchaVO createCaptcha() {
    // 随机生成验证码内容。
    String code = this.randomCode();
    // 返回给前端的验证码标识；真实答案只保存在 Redis 中。
    String captchaId = UUID.randomUUID().toString().replace("-", "");
    stringRedisTemplate.opsForValue().set(
      CAPTCHA_KEY_PREFIX + captchaId,
      code,
      Duration.ofSeconds(this.getCaptchaExpireSeconds())
    );
    return new CaptchaVO(captchaId, this.createImageBase64(code));
  }

  public void validateCaptcha(String captchaId, String captchaCode) {
    // 配置关闭验证码时直接跳过，不影响登录主流程。
    if (!Boolean.TRUE.equals(loginSecurityConf.getCaptchaEnabled())) {
      return;
    }
    if (StrUtil.isAnyBlank(captchaId, captchaCode)) {
      throw new ServiceException("验证码不能为空");
    }
    String key = CAPTCHA_KEY_PREFIX + captchaId;
    // 先取出验证码答案，再立刻删除，确保验证码一次性使用。
    String code = stringRedisTemplate.opsForValue().get(key);
    stringRedisTemplate.delete(key);
    if (StrUtil.isBlank(code) || !code.equalsIgnoreCase(captchaCode.trim())) {
      throw new ServiceException("验证码错误或已过期");
    }
  }

  public void validateNotLocked(String username, Long tenantId) {
    String lockKey = this.lockKey(username, tenantId);
    // 锁定键存在即代表当前账号仍处于锁定窗口内。
    String locked = stringRedisTemplate.opsForValue().get(lockKey);
    if (StrUtil.isNotBlank(locked)) {
      throw new ServiceException(this.buildLockedMessage(lockKey));
    }
  }

  public String recordLoginFailure(String username, Long tenantId) {
    String failKey = this.failKey(username, tenantId);
    // 记录本次失败次数，并刷新失败统计窗口的过期时间。
    Long failCount = stringRedisTemplate.opsForValue().increment(failKey);
    stringRedisTemplate.expire(failKey, Duration.ofMinutes(this.getFailExpireMinutes()));
    // Redis 返回 null 时按保守兜底处理，避免登录流程因提示文案构建失败而中断。
    if (failCount == null) {
      return this.buildLoginFailureMessage(Math.max(this.getMaxFailCount() - 1L, 1L));
    }
    // 还未达到阈值时，直接提示剩余尝试次数。
    long remainingAttempts = (long) this.getMaxFailCount() - failCount;
    if (remainingAttempts > 0) {
      return this.buildLoginFailureMessage(remainingAttempts);
    }
    String lockKey = this.lockKey(username, tenantId);
    // 达到阈值后立即写入锁定标记，并让前端从当前这次失败开始看到锁定提示。
    stringRedisTemplate.opsForValue().set(lockKey, "1", Duration.ofMinutes(this.getLockMinutes()));
    if (failCount >= this.getMaxFailCount()) {
      // 进入锁定后删除失败计数，避免锁定结束后沿用旧计数。
      stringRedisTemplate.delete(failKey);
    }
    // 当前这次失败已经触发锁定，因此直接返回锁定剩余时间提示。
    return this.buildLockedMessage(Duration.ofMinutes(this.getLockMinutes()).getSeconds());
  }

  public void clearLoginFailure(String username, Long tenantId) {
    // 登录成功后同时清除失败计数和锁定标记，保证后续重新开始统计。
    stringRedisTemplate.delete(this.failKey(username, tenantId));
    stringRedisTemplate.delete(this.lockKey(username, tenantId));
  }

  public void validatePasswordStrength(String password) {
    // 密码强度只做基础校验：非空、最小长度、必须包含字母和数字。
    if (StrUtil.isBlank(password)) {
      throw new ServiceException("密码不能为空");
    }
    if (password.length() < this.getMinPasswordLength()) {
      throw new ServiceException("密码长度不能小于" + this.getMinPasswordLength() + "位");
    }
    if (Boolean.TRUE.equals(loginSecurityConf.getRequirePasswordLetter()) && password.chars().noneMatch(Character::isLetter)) {
      throw new ServiceException("密码必须包含字母");
    }
    if (Boolean.TRUE.equals(loginSecurityConf.getRequirePasswordDigit()) && password.chars().noneMatch(Character::isDigit)) {
      throw new ServiceException("密码必须包含数字");
    }
  }

  private String randomCode() {
    // 配置未提供字符集时，使用默认的不易混淆字符集合。
    String chars = loginSecurityConf.getCaptchaChars();
    if (StrUtil.isBlank(chars)) {
      chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    }
    int length = this.getCaptchaLength();
    StringBuilder code = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      code.append(chars.charAt(secureRandom.nextInt(chars.length())));
    }
    return code.toString();
  }

  private String createImageBase64(String code) {
    // 根据配置创建验证码底图尺寸。
    int width = this.getCaptchaWidth();
    int height = this.getCaptchaHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    try {
      // 先绘制白底和噪点，再逐字绘制验证码内容，提升识别难度。
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, width, height);
      this.drawNoise(graphics, width, height);
      graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(24, height - 12)));
      int charWidth = width / (code.length() + 1);
      for (int i = 0; i < code.length(); i++) {
        graphics.setColor(this.randomTextColor());
        int x = charWidth * i + 12;
        int y = height - 10 - secureRandom.nextInt(6);
        int angle = secureRandom.nextInt(25) - 12;
        graphics.rotate(Math.toRadians(angle), x, y);
        graphics.drawString(String.valueOf(code.charAt(i)), x, y);
        graphics.rotate(Math.toRadians(-angle), x, y);
      }
    } finally {
      graphics.dispose();
    }
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      // 直接返回 data:image/png;base64，方便前端直接渲染。
      ImageIO.write(image, "png", outputStream);
      return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    } catch (Exception e) {
      throw new ServiceException("验证码生成失败");
    }
  }

  private void drawNoise(Graphics2D graphics, int width, int height) {
    // 绘制少量干扰线。
    for (int i = 0; i < 8; i++) {
      graphics.setColor(new Color(120 + secureRandom.nextInt(100), 120 + secureRandom.nextInt(100), 120 + secureRandom.nextInt(100)));
      int x1 = secureRandom.nextInt(width);
      int y1 = secureRandom.nextInt(height);
      int x2 = secureRandom.nextInt(width);
      int y2 = secureRandom.nextInt(height);
      graphics.drawLine(x1, y1, x2, y2);
    }
    // 再叠加点状噪声，避免验证码过于规整。
    for (int i = 0; i < 40; i++) {
      graphics.setColor(new Color(160 + secureRandom.nextInt(80), 160 + secureRandom.nextInt(80), 160 + secureRandom.nextInt(80)));
      graphics.fillOval(secureRandom.nextInt(width), secureRandom.nextInt(height), 2, 2);
    }
  }

  private Color randomTextColor() {
    return new Color(secureRandom.nextInt(90), secureRandom.nextInt(90), secureRandom.nextInt(90));
  }

  private String failKey(String username, Long tenantId) {
    // 失败次数按“租户 + 用户名”粒度隔离，避免不同租户间互相影响。
    return FAIL_KEY_PREFIX + this.loginKey(username, tenantId);
  }

  private String lockKey(String username, Long tenantId) {
    // 锁定标记与失败次数使用同一套账号维度。
    return LOCK_KEY_PREFIX + this.loginKey(username, tenantId);
  }

  private String loginKey(String username, Long tenantId) {
    // 用户名统一转小写，避免同一账号因大小写不同产生多套计数。
    return (tenantId == null ? "none" : tenantId) + ":" + username.toLowerCase(Locale.ROOT);
  }

  private String buildLoginFailureMessage(long remainingAttempts) {
    // 登录失败时只返回剩余可尝试次数，不暴露内部计数实现细节。
    return LOGIN_FAIL_MSG_PREFIX + "，还可尝试 " + remainingAttempts + " 次";
  }

  private String buildLockedMessage(String lockKey) {
    // 优先使用 Redis TTL 作为真实剩余锁定时间，避免提示和实际过期时间不一致。
    Long remainingSeconds = stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
    if (remainingSeconds == null || remainingSeconds <= 0) {
      // TTL 取不到时退回到配置时长，至少保证提示文案可用。
      remainingSeconds = Duration.ofMinutes(this.getLockMinutes()).getSeconds();
    }
    return this.buildLockedMessage(remainingSeconds);
  }

  private String buildLockedMessage(long remainingSeconds) {
    // 将剩余秒数格式化为中文时间片段，供锁定提示直接复用。
    return LOGIN_LOCK_MSG_PREFIX + this.formatDuration(remainingSeconds) + "后再试";
  }

  private String formatDuration(long remainingSeconds) {
    // Redis TTL 可能返回 0 或负值，这里统一兜底到至少 1 秒。
    long safeSeconds = Math.max(remainingSeconds, 1L);
    long hours = safeSeconds / 3600;
    long minutes = (safeSeconds % 3600) / 60;
    long seconds = safeSeconds % 60;
    // 优先展示更贴近用户理解的“小时/分钟/秒”中文格式。
    if (hours > 0) {
      if (minutes > 0) {
        return hours + " 小时 " + minutes + " 分钟";
      }
      return hours + " 小时";
    }
    if (minutes > 0 && seconds > 0) {
      return minutes + " 分 " + seconds + " 秒";
    }
    if (minutes > 0) {
      return minutes + " 分钟";
    }
    return seconds + " 秒";
  }

  private int getCaptchaExpireSeconds() {
    // 读取配置时统一加兜底，避免空值或非法值导致运行期异常。
    Integer seconds = loginSecurityConf.getCaptchaExpireSeconds();
    return seconds == null || seconds <= 0 ? 120 : seconds;
  }

  private int getCaptchaWidth() {
    Integer width = loginSecurityConf.getCaptchaWidth();
    return width == null || width <= 0 ? 120 : width;
  }

  private int getCaptchaHeight() {
    Integer height = loginSecurityConf.getCaptchaHeight();
    return height == null || height <= 0 ? 40 : height;
  }

  private int getCaptchaLength() {
    Integer length = loginSecurityConf.getCaptchaLength();
    return length == null || length <= 0 ? 4 : length;
  }

  private int getMaxFailCount() {
    Integer maxFailCount = loginSecurityConf.getMaxFailCount();
    return maxFailCount == null || maxFailCount <= 0 ? 5 : maxFailCount;
  }

  private int getLockMinutes() {
    Integer lockMinutes = loginSecurityConf.getLockMinutes();
    return lockMinutes == null || lockMinutes <= 0 ? 15 : lockMinutes;
  }

  private int getFailExpireMinutes() {
    Integer failExpireMinutes = loginSecurityConf.getFailExpireMinutes();
    return failExpireMinutes == null || failExpireMinutes <= 0 ? 30 : failExpireMinutes;
  }

  private int getMinPasswordLength() {
    Integer minPasswordLength = loginSecurityConf.getMinPasswordLength();
    return minPasswordLength == null || minPasswordLength <= 0 ? 8 : minPasswordLength;
  }
}
