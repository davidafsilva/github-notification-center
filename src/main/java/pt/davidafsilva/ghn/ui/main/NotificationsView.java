package pt.davidafsilva.ghn.ui.main;

import javafx.scene.layout.BorderPane;
import pt.davidafsilva.ghn.model.mutable.Category;

/**
 * @author david
 */
public class NotificationsView extends BorderPane {

  private final NotificationsController controller;
  private LeftSideView leftSideView;
  private ContentsView contentsView;

  NotificationsView(final NotificationsController controller) {
    this.controller = controller;
  }

  void initView() {
    // left side
    setLeft(leftSideView = new LeftSideView(controller));

    // right side
    setRight(contentsView = new ContentsView(controller));
  }

  void updateCategory(final String name, final Category category) {
    leftSideView.updateCategory(name, category);
  }

  void addCategory(final Category category) {
    leftSideView.addCategory(category);
  }

  void removeCategory(final Category category) {
    leftSideView.removeCategory(category);
  }

  boolean hasCategories() {
    return leftSideView.hasCategories();
  }

  void onCategorySaveComplete(final String errorMessage) {
    leftSideView.onCategorySaveComplete(errorMessage);
  }
}
