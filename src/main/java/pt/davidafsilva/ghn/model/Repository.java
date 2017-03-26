package pt.davidafsilva.ghn.model;

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
