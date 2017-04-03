package pt.davidafsilva.ghn.service.options;

import java.util.Locale;

import pt.davidafsilva.ghn.ApplicationOptions;

/**
 * @author david
 */
public class ApplicationOptionsService {

  private final ApplicationOptionsBackend backend;
  private final ApplicationOptions options;

  public ApplicationOptionsService() {
    backend = resolveBackendStrategy();
    options = backend.load();
  }

  public ApplicationOptions getOptions() {
    return options;
  }

  public void save(final ApplicationOptions options) {
    backend.save(options);
  }

  private ApplicationOptionsBackend resolveBackendStrategy() {
    String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    if (os.contains("mac") || os.contains("darwin")) {
      return new OsxKeychainBasedBackend();
    }
    return new DotFileBasedBackend();
  }
}
