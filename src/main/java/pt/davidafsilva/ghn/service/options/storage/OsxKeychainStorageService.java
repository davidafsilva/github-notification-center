package pt.davidafsilva.ghn.service.options.storage;

import org.slf4j.Logger;

import java.util.Optional;

import pt.davidafsilva.apple.OSXKeychain;
import pt.davidafsilva.apple.OSXKeychainException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author david
 */
public class OsxKeychainStorageService implements SecureStorageService {

  private static final Logger LOGGER = getLogger(OsxKeychainStorageService.class);
  private static final String username = System.getProperty("user.name");

  private final OSXKeychain keychain;

  public OsxKeychainStorageService() {
    try {
      this.keychain = OSXKeychain.getInstance();
    } catch (final OSXKeychainException e) {
      throw new IllegalStateException("unable to load OS X keychain");
    }
  }

  @Override
  public boolean write(final String key, final String value) {
    try {
      keychain.modifyGenericPassword(key, username, value);
      return true;
    } catch (final OSXKeychainException e) {
      LOGGER.error("unable to save property", e);
    }
    return false;
  }

  @Override
  public Optional<String> read(final String key) {
    try {
      return keychain.findGenericPassword(key, username);
    } catch (final OSXKeychainException e) {
      LOGGER.error("unable to save property", e);
    }
    return Optional.empty();
  }
}
