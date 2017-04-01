package pt.davidafsilva.ghn;

import java.util.Optional;
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
  private static final String GITHUB_AUTH_TOKEN = "github.token";

  private final Properties p;

  public ApplicationOptions(final Properties p) {
    this.p = p;
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

  public Properties getProperties() {
    return p;
  }

  public Optional<String> getToken() {
    return Optional.ofNullable(p.getProperty(GITHUB_AUTH_TOKEN));
  }

  public void setToken(final String token) {
    p.setProperty(GITHUB_AUTH_TOKEN, token);
  }
}
