package pt.davidafsilva.ghn.model.filter.post;

import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.model.Repository;

/**
 * @author david
 */
class RepositoryPostFilter implements PostFilter {

  private final StringFilter nameFilter;
  private final StringFilter descriptionFilter;

  private RepositoryPostFilter(
      final StringFilter nameFilter,
      final StringFilter descriptionFilter) {
    this.nameFilter = nameFilter;
    this.descriptionFilter = descriptionFilter;
  }

  @Override
  public boolean filter(final Notification notification) {
    final Repository repository = notification.getRepository();
    return !(nameFilter != null && !nameFilter.filter(repository.getName())) &&
        !(descriptionFilter != null && !descriptionFilter.filter(repository.getDescription()));
  }

  static RepositoryPostFilter name(final StringFilter filter) {
    return new RepositoryPostFilter(filter, null);
  }

  static RepositoryPostFilter description(final StringFilter filter) {
    return new RepositoryPostFilter(null, filter);
  }
}
