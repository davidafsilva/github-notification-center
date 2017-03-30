package pt.davidafsilva.ghn.model;

import java.util.Objects;
import java.util.Optional;

/**
 * @author david
 */
public class User {

  private final String username;
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

  public Optional<String> getCredentials() {
    return Optional.ofNullable(credentials);
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

  @Override
  public String toString() {
    return "User{" +
        "username='" + username + '\'' +
        '}';
  }
}
