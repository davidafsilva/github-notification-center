package pt.davidafsilva.ghn.model;

import java.util.Objects;

/**
 * @author david
 */
public class Repository {

  private final Long id;
  private final Owner owner;
  private final String name;
  private final String description;
  private final boolean isPrivate;
  private final boolean fork;
  private final String url;
  private final String htmlUrl;

  private Repository(final Long id, final Owner owner, final String name, final String description,
      final boolean isPrivate, final boolean fork, final String url, final String htmlUrl) {
    this.id = id;
    this.owner = owner;
    this.name = name;
    this.description = description;
    this.isPrivate = isPrivate;
    this.fork = fork;
    this.url = url;
    this.htmlUrl = htmlUrl;
  }

  public Long getId() {
    return id;
  }

  public Owner getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  public boolean isFork() {
    return fork;
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
    final Repository that = (Repository) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(owner, that.owner) &&
        Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, owner, name);
  }

  @Override
  public String toString() {
    return "Repository{" +
        "id=" + id +
        ", owner=" + owner +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", isPrivate=" + isPrivate +
        ", fork=" + fork +
        ", url='" + url + '\'' +
        ", htmlUrl='" + htmlUrl + '\'' +
        '}';
  }

  public static class Builder {

    private Long id;
    private Owner owner;
    private String name;
    private String description;
    private boolean isPrivate;
    private boolean fork;
    private String url;
    private String htmlUrl;

    public Builder setId(final Long id) {
      this.id = id;
      return this;
    }

    public Builder setOwner(final Owner owner) {
      this.owner = owner;
      return this;
    }

    public Builder setName(final String name) {
      this.name = name;
      return this;
    }

    public Builder setDescription(final String description) {
      this.description = description;
      return this;
    }

    public Builder setIsPrivate(final boolean isPrivate) {
      this.isPrivate = isPrivate;
      return this;
    }

    public Builder setFork(final boolean fork) {
      this.fork = fork;
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

    public Repository build() {
      return new Repository(id, owner, name, description, isPrivate, fork, url, htmlUrl);
    }
  }
}
