package pt.davidafsilva.ghn.model.mutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;
import java.util.UUID;

import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;

/**
 * @author david
 */
public class Configuration extends AbstractModel implements Persisted {

  private String id;
  private String githubUrl = "https://api.github.com:443";
  private Duration timeout = Duration.ofSeconds(30);
  @JsonIgnore
  private SecuredConfiguration securedConfiguration = new SecuredConfiguration();

  public Configuration() {
    this(UUID.randomUUID().toString());
  }

  public Configuration(final String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getGithubUrl() {
    return githubUrl;
  }

  public Configuration setGithubUrl(final String githubUrl) {
    this.githubUrl = githubUrl;
    return this;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public Configuration setTimeout(final Duration timeout) {
    this.timeout = timeout;
    return this;
  }

  public SecuredConfiguration getSecuredConfiguration() {
    return securedConfiguration;
  }

  public Configuration setSecuredConfiguration(final SecuredConfiguration securedConfiguration) {
    this.securedConfiguration = securedConfiguration;
    return this;
  }
}
