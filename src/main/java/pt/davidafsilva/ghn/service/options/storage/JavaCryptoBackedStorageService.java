package pt.davidafsilva.ghn.service.options.storage;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * @author david
 */
public class JavaCryptoBackedStorageService extends CryptoBackedStorageService {

  private static final String CIPHER_SETTINGS = "AES/CBC/PKCS5Padding";
  private static final String SECRET_SETTINGS = "PBKDF2WithHmacSHA512";
  private static final String SECRET_ALGORITHM = "AES";
  private static final String HASH_ALGORITHM = "SHA-256";
  private static final int ITERATION_COUNT = 65536;
  private static final int KEY_SIZE = 128;

  private final SecretKeySpec secretKey;
  private final Cipher cipher;

  public JavaCryptoBackedStorageService(final StorageService decorated) {
    super(decorated);
    try {
      // TODO: not safe by any means
      // need to come up with linux/other storage strategy
      final HardwareAbstractionLayer hw = new SystemInfo().getHardware();
      final String key = hw.getComputerSystem().getSerialNumber();
      final String salt = hw.getProcessor().getProcessorID();

      // create the secret factory with the configure settings
      final SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_SETTINGS);

      // create the key from the password and salt
      final KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), ITERATION_COUNT,
          KEY_SIZE);

      // create the secret from the derived key using AES
      secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), SECRET_ALGORITHM);
      cipher = Cipher.getInstance(CIPHER_SETTINGS);
    } catch (final Exception e) {
      LOGGER.error("unable to initialize cipher", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  byte[] cipherData(final byte[] value, final byte[] entropy) {
    try {
      final IvParameterSpec ivParameter = new IvParameterSpec(iv(entropy));
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameter);
      return cipher.doFinal(value);
    } catch (final Exception e) {
      LOGGER.error("unable to cipher data", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  byte[] decipherData(final byte[] value, final byte[] entropy) {
    try {
      final IvParameterSpec ivParameter = new IvParameterSpec(iv(entropy));
      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameter);
      return cipher.doFinal(value);
    } catch (final Exception e) {
      LOGGER.warn("unable to decipher data", e);
      throw new RuntimeException(e);
    }
  }

  private byte[] iv(final byte[] source) throws NoSuchAlgorithmException, DigestException {
    final byte[] iv = new byte[KEY_SIZE / Byte.SIZE];
    final MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
    final byte[] hash = md.digest(source);
    System.arraycopy(hash, 0, iv, 0, iv.length);
    return iv;
  }
}
