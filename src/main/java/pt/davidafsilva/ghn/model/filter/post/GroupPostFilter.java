package pt.davidafsilva.ghn.model.filter.post;

import java.util.Arrays;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
class GroupPostFilter implements PostFilter {

  private final PostFilter[] filters;

  GroupPostFilter(final PostFilter... filters) {
    this.filters = filters;
  }

  @Override
  public boolean filter(final Notification notification) {
    return Arrays.stream(filters).allMatch(f -> f.filter(notification));
  }
}
