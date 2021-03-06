package pt.davidafsilva.ghn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;
import java.util.Optional;

/**
 * @author david
 */
public class User extends AbstractModel {

  private final String username;
  @JsonIgnore
  private final String credentials;
  private final String avatarUrl;

  public User(final String username, final String credentials, final String avatarUrl) {
    this.username = username;
    this.credentials = credentials;
    this.avatarUrl = avatarUrl;
  }

  public String getUsername() {
    return username;
  }

  public Optional<String> getAvatarUrl() {
    return Optional.ofNullable(avatarUrl);
  }

  public String getCredentials() {
    return credentials;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final User user = (User) o;
    return Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
