package pt.davidafsilva.ghn.model.filter.post;

import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.model.Subject;

/**
 * @author david
 */
class SubjectPostFilter implements PostFilter {

  private final StringFilter titleFilter;
  private final StringFilter typeFilter;

  private SubjectPostFilter(
      final StringFilter titleFilter,
      final StringFilter typeFilter) {
    this.titleFilter = titleFilter;
    this.typeFilter = typeFilter;
  }

  @Override
  public boolean filter(final Notification notification) {
    final Subject subject = notification.getSubject();
    return !(titleFilter != null && !titleFilter.filter(subject.getTitle())) &&
        !(typeFilter != null && !typeFilter.filter(subject.getType()));
  }

  static SubjectPostFilter title(final StringFilter filter) {
    return new SubjectPostFilter(filter, null);
  }

  static SubjectPostFilter type(final StringFilter filter) {
    return new SubjectPostFilter(null, filter);
  }
}
