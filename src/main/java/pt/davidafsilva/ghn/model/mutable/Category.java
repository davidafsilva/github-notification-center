package pt.davidafsilva.ghn.model.mutable;

import java.util.UUID;

import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;

/**
 * @author david
 */
public class Category extends AbstractModel implements Persisted {

  private String id;
  private String name;
  private boolean isEditable;
  private boolean isDeletable;
  private int unreadCount;

  public Category(final String name, final boolean isEditable, final boolean isDeletable) {
    this(UUID.randomUUID().toString(), name, isEditable, isDeletable);
  }

  public Category(final String id, final String name, final boolean isEditable,
      final boolean isDeletable) {
    this.id = id;
    this.name = name;
    this.isEditable = isEditable;
    this.isDeletable = isDeletable;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Category setName(final String name) {
    this.name = name;
    return this;
  }

  public boolean isEditable() {
    return isEditable;
  }

  public Category setEditable(final boolean editable) {
    isEditable = editable;
    return this;
  }

  public boolean isDeletable() {
    return isDeletable;
  }

  public Category setDeletable(final boolean deletable) {
    isDeletable = deletable;
    return this;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public Category setUnreadCount(final int unreadCount) {
    this.unreadCount = unreadCount;
    return this;
  }
}
