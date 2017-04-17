package pt.davidafsilva.ghn.model.mutable;

/**
 * @author david
 */
public class Category {

  private String name;
  private boolean isEditable;
  private boolean isDeletable;
  private int unreadCount;

  public Category(final String name, final boolean isEditable, final boolean isDeletable) {
    this.name = name;
    this.isEditable = isEditable;
    this.isDeletable = isDeletable;
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
