package top.zhjh.util;

import org.junit.jupiter.api.Test;
import top.csaf.crypto.DigestUtil;
import top.zhjh.prop.PasswordCipherConf;

import static org.junit.jupiter.api.Assertions.*;

class PasswordCipherTest {

  @Test
  void encodeUsesSaltedHash() {
    PasswordCipher passwordCipher = new PasswordCipher(new PasswordCipherConf());

    String encoded1 = passwordCipher.encode("secret123");
    String encoded2 = passwordCipher.encode("secret123");

    assertTrue(encoded1.startsWith(PasswordCipher.HASH_PREFIX));
    assertTrue(encoded2.startsWith(PasswordCipher.HASH_PREFIX));
    assertNotEquals(encoded1, encoded2);
    assertTrue(passwordCipher.matches("secret123", encoded1));
    assertTrue(passwordCipher.matches("secret123", encoded2));
  }

  @Test
  void matchesLegacySha512AndMarksUpgrade() {
    PasswordCipher passwordCipher = new PasswordCipher(new PasswordCipherConf());
    String legacy = DigestUtil.sha512Hex("secret123");

    assertTrue(passwordCipher.matches("secret123", legacy));
    assertTrue(passwordCipher.shouldUpgradeLegacy(legacy));
  }
}
