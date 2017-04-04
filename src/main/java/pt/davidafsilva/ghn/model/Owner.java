package pt.davidafsilva.ghn.model;

import java.util.Objects;

/**
 * @author david
 */
public class Owner {

  private final Long id;
  private final String login;
  private final String avatarUrl;
  private final String gravatarId;
  private final String url;
  private final String htmlUrl;

  private Owner(final Long id, final String login, final String avatarUrl, final String gravatarId,
      final String url, final String htmlUrl) {
    this.id = id;
    this.login = login;
    this.avatarUrl = avatarUrl;
    this.gravatarId = gravatarId;
    this.url = url;
    this.htmlUrl = htmlUrl;
  }

  public Long getId() {
    return id;
  }

  public String getLogin() {
    return login;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getGravatarId() {
    return gravatarId;
  }

  public String getUrl() {
    return url;
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Owner owner = (Owner) o;
    return Objects.equals(id, owner.id) &&
        Objects.equals(login, owner.login);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, login);
  }

  @Override
  public String toString() {
    return "Owner{" +
        "id=" + id +
        ", login='" + login + '\'' +
        ", avatarUrl='" + avatarUrl + '\'' +
        ", gravatarId='" + gravatarId + '\'' +
        ", url='" + url + '\'' +
        ", htmlUrl='" + htmlUrl + '\'' +
        '}';
  }

  public static class Builder {

    private Long id;
    private String login;
    private String avatarUrl;
    private String gravatarId;
    private String url;
    private String htmlUrl;

    public Builder setId(final Long id) {
      this.id = id;
      return this;
    }

    public Builder setLogin(final String login) {
      this.login = login;
      return this;
    }

    public Builder setAvatarUrl(final String avatarUrl) {
      this.avatarUrl = avatarUrl;
      return this;
    }

    public Builder setGravatarId(final String gravatarId) {
      this.gravatarId = gravatarId;
      return this;
    }

    public Builder setUrl(final String url) {
      this.url = url;
      return this;
    }

    public Builder setHtmlUrl(final String htmlUrl) {
      this.htmlUrl = htmlUrl;
      return this;
    }

    public Owner build() {
      return new Owner(id, login, avatarUrl, gravatarId, url, htmlUrl);
    }
  }
}
