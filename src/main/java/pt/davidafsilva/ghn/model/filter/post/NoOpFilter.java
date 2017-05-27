package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonProperty;

import pt.davidafsilva.ghn.model.Notification;

/**
 * @author david
 */
class NoOpFilter implements PostFilter {

  @Override
  public boolean filter(final Notification notification) {
    return true;
  }

  @Override
  @JsonProperty
  public PostFilterType getType() {
    return PostFilterType.NO_OP;
  }
}
