package pt.davidafsilva.ghn.model;

/**
 * @author david
 */
public final class Subject {

  private String title;
  private String url;
  private String latestCommentUrl;
  private String type;

  private Subject(final String title, final String url, final String latestCommentUrl,
      final String type) {
    this.title = title;
    this.url = url;
    this.latestCommentUrl = latestCommentUrl;
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getLatestCommentUrl() {
    return latestCommentUrl;
  }

  public String getType() {
    return type;
  }

  public class Builder {

    private String title;
    private String url;
    private String latestCommentUrl;
    private String type;

    public Builder setTitle(final String title) {
      this.title = title;
      return this;
    }

    public Builder setUrl(final String url) {
      this.url = url;
      return this;
    }

    public Builder setLatestCommentUrl(final String latestCommentUrl) {
      this.latestCommentUrl = latestCommentUrl;
      return this;
    }

    public Builder setType(final String type) {
      this.type = type;
      return this;
    }

    public Subject build() {
      return new Subject(title, url, latestCommentUrl, type);
    }
  }
}
