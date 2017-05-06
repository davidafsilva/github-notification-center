package pt.davidafsilva.ghn.model.filter.pre;

import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * @author david
 */
public final class PreFilterBuilder {

  private boolean all;
  private boolean participating;
  private Supplier<LocalDateTime> from;
  private Supplier<LocalDateTime> to;

  PreFilterBuilder() {}

  public PreFilterBuilder includeRead(boolean include) {
    this.all = include;
    return this;
  }

  public PreFilterBuilder participatingOnly(boolean participating) {
    this.participating = participating;
    return this;
  }

  public PreFilterBuilder setFrom(final Supplier<LocalDateTime> from) {
    this.from = from;
    return this;
  }

  public PreFilterBuilder setTo(final Supplier<LocalDateTime> to) {
    this.to = to;
    return this;
  }

  public PreFilter build() {
    return new NotificationRequestFilter(all, participating, from, to);
  }
}
