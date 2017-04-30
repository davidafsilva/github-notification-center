package pt.davidafsilva.ghn.service.storage;

import oshi.SystemInfo;

/**
 * @author david
 */
public final class StorageServiceFactory {

  private final StorageService storageService;
  private final SecureStorageService secureStorageService;

  public StorageServiceFactory() {
    storageService = new CouchbaseFileBackedStorageService();
    secureStorageService = resolveSecureStorage(storageService);
  }

  public StorageService createUnsecuredStorage() {
    return storageService;
  }

  public SecureStorageService createSecuredStorage() {
    return secureStorageService;
  }

  private SecureStorageService resolveSecureStorage(final StorageService defaultStorage) {
    final SecureStorageService secureStorageService;

    switch (SystemInfo.getCurrentPlatformEnum()) {
      case MACOSX:
        secureStorageService = new OsxKeychainStorageService();
        break;
      case WINDOWS:
        secureStorageService = new Win32CryptoBackedStorageService(defaultStorage);
        break;
      default:
        secureStorageService = new JavaCryptoBackedStorageService(defaultStorage);
        break;
    }

    return secureStorageService;
  }
}
