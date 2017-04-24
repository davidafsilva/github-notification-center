package pt.davidafsilva.ghn.service.options.storage;

import static com.sun.jna.platform.win32.Crypt32Util.cryptProtectData;
import static com.sun.jna.platform.win32.Crypt32Util.cryptUnprotectData;

/**
 * @author david
 */
public class Win32CryptoBackedStorageService extends CryptoBackedStorageService {

  public Win32CryptoBackedStorageService(final StorageService decorated) {
    super(decorated);
  }

  @Override
  byte[] cipherData(final byte[] value, final byte[] entropy) {
    return cryptProtectData(value, entropy, 0, "", null);
  }

  @Override
  byte[] decipherData(final byte[] value, final byte[] entropy) {
    return cryptUnprotectData(value, entropy, 0, null);
  }

}
