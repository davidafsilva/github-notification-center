package pt.davidafsilva.ghn.model.filter.post;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
class OrPostFilter implements PostFilter {

  private final PostFilter left;
  private final PostFilter right;

  OrPostFilter(final PostFilter left, final PostFilter right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean filter(final Notification notification) {
    return left.filter(notification) || right.filter(notification);
  }
}
