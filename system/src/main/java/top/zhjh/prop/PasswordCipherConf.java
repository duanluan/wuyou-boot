package top.zhjh.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "wuyou.security.password")
@Component
@Data
public class PasswordCipherConf {

  private Boolean enabled = true;
  private String algorithm = "PBKDF2WithHmacSHA256";
  private Integer iterations = 120000;
  private Integer saltLength = 16;
  private Integer hashLength = 32;
}
