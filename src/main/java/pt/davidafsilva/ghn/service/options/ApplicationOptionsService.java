package pt.davidafsilva.ghn.service.options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.davidafsilva.ghn.ApplicationOptions;

/**
 * @author david
 */
public class ApplicationOptionsService {

  private static final Logger LOGGER = Logger.getLogger(ApplicationOptionsService.class.getName());
  private static final String HOME = System.getProperty("user.home");
  private static final String COMMENT = "GHN Hub configuration";

  private final ApplicationOptions options;

  {
    final Properties p = new Properties();
    try {
      final Path configFile = getFileLocation();
      if (Files.exists(configFile)) {
        p.load(Files.newInputStream(configFile));
      }
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "unable to read application properties: " + e.getMessage());
    }
    options = new ApplicationOptions(p);
  }

  public ApplicationOptions getOptions() {
    return options;
  }

  public void save(final ApplicationOptions options) {
    try {
      final Path configFile = getFileLocation();
      options.getProperties().store(Files.newOutputStream(configFile), COMMENT);
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "unable to save application properties");
    }
  }

  private Path getFileLocation() {
    return Paths.get(HOME, ".ghnc").toAbsolutePath();
  }
}
