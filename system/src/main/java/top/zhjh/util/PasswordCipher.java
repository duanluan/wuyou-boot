package top.zhjh.util;

import org.springframework.stereotype.Component;
import top.csaf.crypto.DigestUtil;
import top.csaf.lang.StrUtil;
import top.zhjh.exception.ServiceException;
import top.zhjh.prop.PasswordCipherConf;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

@Component
public class PasswordCipher {

  static final String HASH_PREFIX = "{PBKDF2}";
  private static final String DELIMITER = "$";
  private static final Pattern LEGACY_SHA512 = Pattern.compile("^[a-fA-F0-9]{128}$");
  private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
  private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

  private final PasswordCipherConf passwordCipherConf;
  private final SecureRandom secureRandom = new SecureRandom();

  public PasswordCipher(PasswordCipherConf passwordCipherConf) {
    this.passwordCipherConf = passwordCipherConf;
  }

  public String encode(String rawPassword) {
    if (StrUtil.isBlank(rawPassword)) {
      throw new ServiceException("密码不能为空");
    }
    if (!Boolean.TRUE.equals(passwordCipherConf.getEnabled())) {
      return DigestUtil.sha512Hex(rawPassword);
    }
    byte[] salt = new byte[getSaltLength()];
    secureRandom.nextBytes(salt);
    byte[] hash = hash(rawPassword.toCharArray(), salt, getIterations(), getHashLength(), passwordCipherConf.getAlgorithm());
    return HASH_PREFIX
      + passwordCipherConf.getAlgorithm()
      + DELIMITER
      + getIterations()
      + DELIMITER
      + BASE64_ENCODER.encodeToString(salt)
      + DELIMITER
      + BASE64_ENCODER.encodeToString(hash);
  }

  public boolean matches(String rawPassword, String storedPassword) {
    if (StrUtil.isAnyBlank(rawPassword, storedPassword)) {
      return false;
    }
    if (storedPassword.startsWith(HASH_PREFIX)) {
      return matchesPbkdf2(rawPassword, storedPassword);
    }
    if (isLegacySha512(storedPassword)) {
      return DigestUtil.sha512Hex(rawPassword).equalsIgnoreCase(storedPassword);
    }
    return false;
  }

  public boolean isLegacySha512(String storedPassword) {
    return StrUtil.isNotBlank(storedPassword) && LEGACY_SHA512.matcher(storedPassword).matches();
  }

  public boolean shouldUpgradeLegacy(String storedPassword) {
    if (!Boolean.TRUE.equals(passwordCipherConf.getEnabled()) || StrUtil.isBlank(storedPassword)) {
      return false;
    }
    if (storedPassword.startsWith(HASH_PREFIX)) {
      return false;
    }
    return isLegacySha512(storedPassword);
  }

  private boolean matchesPbkdf2(String rawPassword, String storedPassword) {
    try {
      String[] parts = storedPassword.substring(HASH_PREFIX.length()).split(Pattern.quote(DELIMITER), -1);
      if (parts.length != 4) {
        return false;
      }
      String algorithm = parts[0];
      int iterations = Integer.parseInt(parts[1]);
      byte[] salt = BASE64_DECODER.decode(parts[2]);
      byte[] expectedHash = BASE64_DECODER.decode(parts[3]);
      byte[] actualHash = hash(rawPassword.toCharArray(), salt, iterations, expectedHash.length, algorithm);
      return MessageDigest.isEqual(actualHash, expectedHash);
    } catch (Exception e) {
      return false;
    }
  }

  private int getIterations() {
    Integer iterations = passwordCipherConf.getIterations();
    if (iterations == null || iterations <= 0) {
      throw new ServiceException("密码哈希参数无效");
    }
    return iterations;
  }

  private int getSaltLength() {
    Integer saltLength = passwordCipherConf.getSaltLength();
    if (saltLength == null || saltLength <= 0) {
      throw new ServiceException("密码哈希参数无效");
    }
    return saltLength;
  }

  private int getHashLength() {
    Integer hashLength = passwordCipherConf.getHashLength();
    if (hashLength == null || hashLength <= 0) {
      throw new ServiceException("密码哈希参数无效");
    }
    return hashLength;
  }

  private byte[] hash(char[] rawPassword, byte[] salt, int iterations, int hashLength, String algorithm) {
    try {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
      KeySpec keySpec = new PBEKeySpec(rawPassword, salt, iterations, hashLength * 8);
      return secretKeyFactory.generateSecret(keySpec).getEncoded();
    } catch (Exception e) {
      throw new ServiceException("密码哈希失败");
    }
  }
}
