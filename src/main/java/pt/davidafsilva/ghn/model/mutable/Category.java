package pt.davidafsilva.ghn.model.mutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;
import pt.davidafsilva.ghn.model.filter.post.PostFilter;

/**
 * @author david
 */
public class Category extends AbstractModel implements Persisted {

  private String id;
  private String name;
  private boolean isEditable;
  private boolean isDeletable;
  private int unreadCount;
  private PostFilter postFilter;

  @JsonCreator
  public Category(@JsonProperty(value = "id", required = true) final String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public boolean isEditable() {
    return isEditable;
  }

  public void setEditable(final boolean editable) {
    isEditable = editable;
  }

  public boolean isDeletable() {
    return isDeletable;
  }

  public void setDeletable(final boolean deletable) {
    isDeletable = deletable;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(final int unreadCount) {
    this.unreadCount = unreadCount;
  }

  public Optional<PostFilter> getPostFilter() {
    return Optional.ofNullable(postFilter);
  }

  public void setPostFilter(final PostFilter postFilter) {
    this.postFilter = postFilter;
  }
}
