package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.model.Subject;

/**
 * @author david
 */
class SubjectPostFilter implements PostFilter {

  @JsonProperty
  private final PostFilterType type;
  @JsonProperty
  private final StringFilter titleFilter;
  @JsonProperty
  private final StringFilter typeFilter;

  @JsonCreator
  private SubjectPostFilter(
      @JsonProperty(value = "type", required = true) final PostFilterType type,
      @JsonProperty(value = "titleFilter") final StringFilter titleFilter,
      @JsonProperty(value = "typeFilter") final StringFilter typeFilter) {
    this.type = type;
    this.titleFilter = titleFilter;
    this.typeFilter = typeFilter;
  }

  @Override
  public PostFilterType getType() {
    return type;
  }

  @Override
  public void accept(final PostFilterVisitor visitor) {
    if (titleFilter != null) {
      visitor.notificationTitle(titleFilter.getType(), titleFilter.getComparisonValue());
    }
    if (typeFilter != null) {
      visitor.notificationTitle(typeFilter.getType(), typeFilter.getComparisonValue());
    }
  }

  @Override
  public boolean filter(final Notification notification) {
    final Subject subject = notification.getSubject();
    return !(titleFilter != null && !titleFilter.filter(subject.getTitle())) &&
        !(typeFilter != null && !typeFilter.filter(subject.getType()));
  }

  static SubjectPostFilter title(final StringFilter filter) {
    return new SubjectPostFilter(PostFilterType.NOTIFICATION_TITLE, filter, null);
  }

  static SubjectPostFilter type(final StringFilter filter) {
    return new SubjectPostFilter(PostFilterType.NOTIFICATION_TYPE, null, filter);
  }
}
