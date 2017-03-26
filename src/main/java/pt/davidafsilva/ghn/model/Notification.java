package pt.davidafsilva.ghn.model;

import java.time.LocalDateTime;

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

  public class Builder {

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

    public Builder setRepository(final Repository repository) {
      this.repository = repository;
      return this;
    }

    public Builder setSubject(final Subject subject) {
      this.subject = subject;
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
