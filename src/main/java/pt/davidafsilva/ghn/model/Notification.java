package pt.davidafsilva.ghn.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author david
 */
public final class Notification {

  private final Long id;
  private final Repository repository;
  private final Subject subject;
  private final String reason;
  private final boolean unread;
  private final LocalDateTime updatedAt;
  private final LocalDateTime lastReadAt;
  private final String url;

  private Notification(final Long id, final Repository repository, final Subject subject,
      final String reason, final boolean unread, final LocalDateTime updatedAt,
      final LocalDateTime lastReadAt, final String url) {
    this.id = id;
    this.repository = repository;
    this.subject = subject;
    this.reason = reason;
    this.unread = unread;
    this.updatedAt = updatedAt;
    this.lastReadAt = lastReadAt;
    this.url = url;
  }

  public Long getId() {
    return id;
  }

  public Repository getRepository() {
    return repository;
  }

  public Subject getSubject() {
    return subject;
  }

  public String getReason() {
    return reason;
  }

  public boolean isUnread() {
    return unread;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getLastReadAt() {
    return lastReadAt;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Notification that = (Notification) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(repository, that.repository) &&
        Objects.equals(subject, that.subject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, repository, subject);
  }

  @Override
  public String toString() {
    return "Notification{" +
        "id=" + id +
        ", repository=" + repository +
        ", subject=" + subject +
        ", reason='" + reason + '\'' +
        ", unread=" + unread +
        ", updatedAt=" + updatedAt +
        ", lastReadAt=" + lastReadAt +
        ", url='" + url + '\'' +
        '}';
  }

  public static class Builder {

    private Long id;
    private Repository repository;
    private Subject subject;
    private String reason;
    private boolean unread;
    private LocalDateTime updatedAt;
    private LocalDateTime lastReadAt;
    private String url;

    public Builder setId(final Long id) {
      this.id = id;
      return this;
    }

    public Builder setRepository(final Repository.Builder repository) {
      this.repository = repository.build();
      return this;
    }

    public Builder setSubject(final Subject.Builder subject) {
      this.subject = subject.build();
      return this;
    }

    public Builder setReason(final String reason) {
      this.reason = reason;
      return this;
    }

    public Builder setUnread(final boolean unread) {
      this.unread = unread;
      return this;
    }

    public Builder setUpdatedAt(final LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder setLastReadAt(final LocalDateTime lastReadAt) {
      this.lastReadAt = lastReadAt;
      return this;
    }

    public Builder setUrl(final String url) {
      this.url = url;
      return this;
    }

    public Notification build() {
      return new Notification(id, repository, subject, reason, unread, updatedAt, lastReadAt, url);
    }
  }
}
