package pt.davidafsilva.ghn.model;

import java.util.Objects;

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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Subject subject = (Subject) o;
    return Objects.equals(title, subject.title) &&
        Objects.equals(url, subject.url) &&
        Objects.equals(type, subject.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, url, type);
  }

  @Override
  public String toString() {
    return "Subject{" +
        "title='" + title + '\'' +
        ", url='" + url + '\'' +
        ", latestCommentUrl='" + latestCommentUrl + '\'' +
        ", type='" + type + '\'' +
        '}';
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
