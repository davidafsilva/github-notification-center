package pt.davidafsilva.ghn.model.filter.post;

import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.model.Owner;

/**
 * @author david
 */
class OwnerPostFilter implements PostFilter {

  private final StringFilter userFilter;

  private OwnerPostFilter(final StringFilter userFilter) {
    this.userFilter = userFilter;
  }

  @Override
  public boolean filter(final Notification notification) {
    final Owner owner = notification.getRepository().getOwner();
    return userFilter == null || userFilter.filter(owner.getLogin());
  }

  static OwnerPostFilter owner(final StringFilter filter) {
    return new OwnerPostFilter(filter);
  }
}
