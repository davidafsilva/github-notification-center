package pt.davidafsilva.ghn.service.notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author david
 */
public class NotificationFilter {

  private final boolean all;
  private final boolean participating;
  private final Supplier<LocalDateTime> from;
  private final Supplier<LocalDateTime> to;

  private NotificationFilter(final boolean all, final boolean participating,
      final Supplier<LocalDateTime> from, final Supplier<LocalDateTime> to) {
    this.all = all;
    this.participating = participating;
    this.from = from;
    this.to = to;
  }

  String addParametersTo(final String baseUrl) {
    final StringBuilder sb = new StringBuilder(baseUrl)
        .append("?all=").append(all)
        .append("&participating=").append(participating);
    Optional.ofNullable(from)
        .map(Supplier::get)
        .map(DateTimeFormatter.ISO_DATE_TIME::format)
        .ifPresent(date -> sb.append("&since=").append(date));
    Optional.ofNullable(to)
        .map(Supplier::get)
        .map(DateTimeFormatter.ISO_DATE_TIME::format)
        .ifPresent(date -> sb.append("&before=").append(date));
    return sb.toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private boolean all;
    private boolean participating;
    private Supplier<LocalDateTime> from;
    private Supplier<LocalDateTime> to;

    public Builder includeRead(boolean include) {
      this.all = include;
      return this;
    }

    public Builder participatingOnly(boolean participating) {
      this.participating = participating;
      return this;
    }

    public Builder setFrom(final Supplier<LocalDateTime> from) {
      this.from = from;
      return this;
    }

    public Builder setTo(final Supplier<LocalDateTime> to) {
      this.to = to;
      return this;
    }

    public NotificationFilter build() {
      return new NotificationFilter(all, participating, from, to);
    }
  }

}
