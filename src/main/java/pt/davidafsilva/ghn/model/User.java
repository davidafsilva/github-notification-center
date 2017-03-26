package pt.davidafsilva.ghn.model;

import java.util.Objects;

/**
 * @author david
 */
public class User {

  private final String username;
  private final String token;

  public User(final String username, final String token) {
    this.username = username;
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public String getToken() {
    return token;
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
    return Objects.equals(username, user.username) &&
        Objects.equals(token, user.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, token);
  }

  @Override
  public String toString() {
    return "User{" +
        "username='" + username + '\'' +
        ", token='" + token + '\'' +
        '}';
  }
}
