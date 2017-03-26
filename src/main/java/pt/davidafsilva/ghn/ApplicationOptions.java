package pt.davidafsilva.ghn;

import java.io.IOException;
import java.util.Properties;

/**
 * @author david
 */
public final class ApplicationOptions {

  private static final String GITHUB_SCHEME = "github.scheme";
  private static final String DEFAULT_GITHUB_SCHEME = "https";
  private static final String GITHUB_HOST = "github.host";
  private static final String DEFAULT_GITHUB_HOST = "api.github.com";
  private static final String GITHUB_PORT = "github.port";
  private static final String DEFAULT_GITHUB_PORT = "443";

  private static final Properties p = new Properties();
  static {
    try {
      p.load(ApplicationOptions.class.getResourceAsStream("/application.properties"));
    } catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public String getGitHubScheme() {
    return p.getProperty(GITHUB_SCHEME, DEFAULT_GITHUB_SCHEME);
  }

  public String getGitHubHost() {
    return p.getProperty(GITHUB_HOST, DEFAULT_GITHUB_HOST);
  }

  public int getGitHubPort() {
    return Integer.parseInt(p.getProperty(GITHUB_PORT, DEFAULT_GITHUB_PORT));
  }
}
