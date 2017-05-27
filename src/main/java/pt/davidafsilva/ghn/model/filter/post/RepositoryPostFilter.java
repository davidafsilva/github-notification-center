package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.model.Repository;

/**
 * @author david
 */
class RepositoryPostFilter implements PostFilter {

  @JsonProperty
  private final PostFilterType type;
  @JsonProperty
  private final StringFilter nameFilter;
  @JsonProperty
  private final StringFilter descriptionFilter;
  @JsonProperty
  private final StringFilter userFilter;

  @JsonCreator
  private RepositoryPostFilter(
      @JsonProperty(value = "type", required = true) final PostFilterType type,
      @JsonProperty(value = "nameFilter") final StringFilter nameFilter,
      @JsonProperty(value = "descriptionFilter") final StringFilter descriptionFilter,
      @JsonProperty(value = "userFilter") final StringFilter userFilter) {
    this.type = type;
    this.nameFilter = nameFilter;
    this.descriptionFilter = descriptionFilter;
    this.userFilter = userFilter;
  }

  @Override
  public PostFilterType getType() {
    return type;
  }

  @Override
  public boolean filter(final Notification notification) {
    final Repository repository = notification.getRepository();
    return !(nameFilter != null && !nameFilter.filter(repository.getName())) &&
        !(descriptionFilter != null && !descriptionFilter.filter(repository.getDescription())) &&
        !(userFilter != null && !userFilter.filter(repository.getOwner().getLogin()));
  }

  static RepositoryPostFilter name(final StringFilter filter) {
    return new RepositoryPostFilter(PostFilterType.REPOSITORY_NAME, filter, null, null);
  }

  static RepositoryPostFilter description(final StringFilter filter) {
    return new RepositoryPostFilter(PostFilterType.REPOSITORY_DESCRIPTION, null, filter, null);
  }

  static RepositoryPostFilter owner(final StringFilter filter) {
    return new RepositoryPostFilter(PostFilterType.REPOSITORY_OWNER, null, null, filter);
  }

}
