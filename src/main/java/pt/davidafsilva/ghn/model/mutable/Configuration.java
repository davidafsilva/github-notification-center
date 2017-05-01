package pt.davidafsilva.ghn.model.mutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

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

  @JsonCreator
  public Configuration(@JsonProperty(value = "id", required = true) final String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getGithubUrl() {
    return githubUrl;
  }

  public void setGithubUrl(final String githubUrl) {
    this.githubUrl = githubUrl;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(final Duration timeout) {
    this.timeout = timeout;
  }

  public SecuredConfiguration getSecuredConfiguration() {
    return securedConfiguration;
  }

  public void setSecuredConfiguration(final SecuredConfiguration securedConfiguration) {
    this.securedConfiguration = securedConfiguration;
  }
}
