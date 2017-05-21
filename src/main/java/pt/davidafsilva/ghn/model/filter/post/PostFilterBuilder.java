package pt.davidafsilva.ghn.model.filter.post;

import java.io.Serializable;
import java.util.Stack;
import java.util.function.BinaryOperator;

/**
 * @author david
 */
public class PostFilterBuilder {

  private static final PostFilter NO_FILTER = n->true;

  // the initial filter
  private FilterOperator operator = PostFilter::and;
  private Stack<PostFilter> filterGroups = new Stack<>();
  {
    openGroup();
  }

  private PostFilter finalFilter;

  public PostFilterBuilder withOperator(final FilterOperator operator) {
    this.operator = operator;
    return this;
  }

  public PostFilterBuilder openGroup() {
    filterGroups.push(NO_FILTER);
    return this;
  }

  public PostFilterBuilder closeGroup() {
    if (filterGroups.isEmpty()) {
      throw new IllegalStateException("no groups were found to be ended");
    }

    final PostFilter groupFilter = filterGroups.pop();
    finalFilter = finalFilter == null ? groupFilter : operator.apply(finalFilter, groupFilter);
    return this;
  }

  public PostFilterBuilder and(final PostFilter other) {
    return addFilter(PostFilter::and, other);
  }

  public PostFilterBuilder or(final PostFilter other) {
    return addFilter(PostFilter::or, other);
  }

  public PostFilterBuilder repoOwner(final StringFilter owner) {
    return repoOwner(operator, owner);
  }

  public PostFilterBuilder repoOwner(final FilterOperator operator, final StringFilter owner) {
    return addFilter(operator, OwnerPostFilter.owner(owner));
  }

  public PostFilterBuilder repoName(final StringFilter name) {
    return repoName(operator, name);
  }

  public PostFilterBuilder repoName(final FilterOperator operator, final StringFilter name) {
    return addFilter(operator, RepositoryPostFilter.name(name));
  }

  public PostFilterBuilder repoDescription(final StringFilter description) {
    return repoDescription(description);
  }

  public PostFilterBuilder repoDescription(final FilterOperator operator, final StringFilter description) {
    return addFilter(operator, RepositoryPostFilter.description(description));
  }

  public PostFilterBuilder title(final StringFilter title) {
    return title(operator, title);
  }

  public PostFilterBuilder title(final FilterOperator operator, final StringFilter title) {
    return addFilter(operator, SubjectPostFilter.title(title));
  }

  public PostFilterBuilder type(final StringFilter type) {
    return type(operator, type);
  }

  public PostFilterBuilder type(final FilterOperator operator, final StringFilter type) {
    return addFilter(operator, SubjectPostFilter.type(type));
  }

  private PostFilterBuilder addFilter(final FilterOperator operator, final PostFilter filter) {
    filterGroups.push(operator.apply(filterGroups.pop(), filter));
    return this;
  }

  public PostFilter build() {
    closeGroup();
    return finalFilter;
  }

  public interface FilterOperator extends BinaryOperator<PostFilter>, Serializable {}
}
