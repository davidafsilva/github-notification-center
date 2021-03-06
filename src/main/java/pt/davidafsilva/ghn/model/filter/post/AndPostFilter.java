package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
class AndPostFilter implements PostFilter {

  @JsonProperty
  private final PostFilter left;
  @JsonProperty
  private final PostFilter right;

  @JsonCreator
  AndPostFilter(
      @JsonProperty(value = "left", required = true) final PostFilter left,
      @JsonProperty(value = "right", required = true) final PostFilter right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean filter(final Notification notification) {
    return left.filter(notification) && right.filter(notification);
  }

  @Override
  @JsonIgnore
  public PostFilterType getType() {
    return PostFilterType.AND;
  }

  @Override
  public void accept(final PostFilterVisitor visitor) {
    left.accept(visitor);
    visitor.and(left, right);
    right.accept(visitor);
  }
}
