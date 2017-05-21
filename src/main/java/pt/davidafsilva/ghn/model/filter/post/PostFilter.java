package pt.davidafsilva.ghn.model.filter.post;

import java.io.Serializable;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
public interface PostFilter extends Serializable {

  boolean filter(final Notification notification);

  default PostFilter and(final PostFilter other) {
    return new AndPostFilter(this, other);
  }

  default PostFilter or(final PostFilter other) {
    return new OrPostFilter(this, other);
  }
}
