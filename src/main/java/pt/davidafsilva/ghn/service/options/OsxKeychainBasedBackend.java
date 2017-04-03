package pt.davidafsilva.ghn.service.options;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.service.options.osx.OSXKeychain;
import pt.davidafsilva.ghn.service.options.osx.OSXKeychainException;

/**
 * @author david
 */
class OsxKeychainBasedBackend implements ApplicationOptionsBackend {

  private static final Logger LOGGER = Logger.getLogger(OsxKeychainBasedBackend.class.getName());
  private static final String username = System.getProperty("user.name");
  private static final String GHNC_GITHUB_TOKEN = "ghnc.github.token";

  private final OSXKeychain keychain;

  OsxKeychainBasedBackend() {
    try {
      this.keychain = OSXKeychain.getInstance();
    } catch (final OSXKeychainException e) {
      throw new IllegalStateException("unable to load OS X keychain");
    }
  }

  @Override
  public ApplicationOptions load() {
    final Properties p = new Properties();
    try {
      loadPropertiesFromKeyChain(p);
    } catch (final OSXKeychainException e) {
      LOGGER.log(Level.WARNING, "unable to read application properties: " + e.getMessage());
    }

    return new ApplicationOptions(p);
  }

  @Override
  public void save(final ApplicationOptions options) {
    final String token = options.getToken().orElse("");
    try {
      keychain.modifyGenericPassword(GHNC_GITHUB_TOKEN, username, token);
    } catch (final OSXKeychainException e) {
      LOGGER.log(Level.WARNING, "unable to save application properties");
    }
  }

  private void loadPropertiesFromKeyChain(final Properties p) throws OSXKeychainException {
    keychain.findGenericPassword(GHNC_GITHUB_TOKEN, username)
        .ifPresent(token -> p.setProperty(ApplicationOptions.GITHUB_AUTH_TOKEN, token));
  }
}
