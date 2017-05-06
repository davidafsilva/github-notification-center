package pt.davidafsilva.ghn.model.filter.pre;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author david
 */
class NotificationRequestFilter implements PreFilter {

  private final boolean all;
  private final boolean participating;
  private final Supplier<LocalDateTime> from;
  private final Supplier<LocalDateTime> to;

  NotificationRequestFilter(final boolean all, final boolean participating,
      final Supplier<LocalDateTime> from, final Supplier<LocalDateTime> to) {
    this.all = all;
    this.participating = participating;
    this.from = from;
    this.to = to;
  }

  @Override
  public String appendTo(final String url) {
    final StringBuilder sb = new StringBuilder(url)
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
}
