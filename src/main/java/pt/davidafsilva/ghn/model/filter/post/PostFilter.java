package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface PostFilter {

  boolean filter(final Notification notification);

  PostFilterType getType();

  default PostFilter and(final PostFilter other) {
    return new AndPostFilter(this, other);
  }

  default PostFilter or(final PostFilter other) {
    return new OrPostFilter(this, other);
  }
}
