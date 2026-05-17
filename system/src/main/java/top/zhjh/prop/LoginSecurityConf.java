package top.zhjh.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "wuyou.security.login")
@Component
@Data
public class LoginSecurityConf {

  // 是否开启验证码校验；关闭后只保留失败锁定与密码强度能力。
  private Boolean captchaEnabled = true;
  // 验证码在 Redis 中的有效期，过期后必须重新获取。
  private Integer captchaExpireSeconds = 120;
  // 验证码图片宽度。
  private Integer captchaWidth = 120;
  // 验证码图片高度。
  private Integer captchaHeight = 40;
  // 验证码字符数量。
  private Integer captchaLength = 4;
  // 验证码字符集，默认去掉易混淆字符。
  private String captchaChars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  // 连续失败达到该次数后立即锁定账号登录。
  private Integer maxFailCount = 5;
  // 锁定持续时间，单位分钟。
  private Integer lockMinutes = 15;
  // 失败计数的统计窗口，超过该时间未继续失败则自动清零。
  private Integer failExpireMinutes = 30;
  // 最小密码长度限制。
  private Integer minPasswordLength = 8;
  // 是否要求密码至少包含一个字母。
  private Boolean requirePasswordLetter = true;
  // 是否要求密码至少包含一个数字。
  private Boolean requirePasswordDigit = true;
}
