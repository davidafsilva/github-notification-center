package pt.davidafsilva.ghn.service.options;

import java.util.Locale;

import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.service.options.storage.JavaCryptoBackedStorageService;
import pt.davidafsilva.ghn.service.options.storage.OsxKeychainStorageService;
import pt.davidafsilva.ghn.service.options.storage.SecureStorageService;
import pt.davidafsilva.ghn.service.options.storage.StorageService;
import pt.davidafsilva.ghn.service.options.storage.Win32CryptoBackedStorageService;

/**
 * @author david
 */
public class ApplicationOptionsService {

  private final StorageService storageService;
  private final SecureStorageService secureStorageService;

  private ApplicationOptions options;

  public ApplicationOptionsService() {
    // FIXME: impl
    storageService = null;
    secureStorageService = resolveSecureStorage(storageService);
  }

  public ApplicationOptions load() {
    if (options == null) {
      // FIXME: load options
    }
    return options;
  }

  public void save(final ApplicationOptions options) {
    // FIXME: save options
  }

  private SecureStorageService resolveSecureStorage(final StorageService defaultStorage) {
    final SecureStorageService secureStorageService;
    final String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

    // detect OSX
    if (os.startsWith("mac") || os.startsWith("darwin")) {
      secureStorageService = new OsxKeychainStorageService();
    }
    // windows
    else if (os.startsWith("windows")) {
      secureStorageService = new Win32CryptoBackedStorageService(defaultStorage);
    }
    // linux/other
    else {
      secureStorageService = new JavaCryptoBackedStorageService(defaultStorage);
    }

    return secureStorageService;
  }
}
