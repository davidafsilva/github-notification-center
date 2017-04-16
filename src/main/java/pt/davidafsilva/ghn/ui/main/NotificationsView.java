package pt.davidafsilva.ghn.ui.main;

import javafx.scene.layout.BorderPane;

/**
 * @author david
 */
public class NotificationsView extends BorderPane {


  private final NotificationsController controller;

  NotificationsView(final NotificationsController controller) {
    this.controller = controller;
  }

  void initView() {
    // left side
    setLeft(new LeftSideView(controller));

    // right side
    setRight(new ContentsView(controller));
  }
}
