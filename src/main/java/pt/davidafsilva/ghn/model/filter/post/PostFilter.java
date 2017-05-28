package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface PostFilter {

  PostFilter NO_OP = new NoOpFilter();

  boolean filter(final Notification notification);

  PostFilterType getType();

  void accept(final PostFilterVisitor visitor);

  default PostFilter and(final PostFilter other) {
    return new AndPostFilter(this, other);
  }

  default PostFilter or(final PostFilter other) {
    return new OrPostFilter(this, other);
  }
}
