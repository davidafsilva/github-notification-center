package pt.davidafsilva.ghn.ui.main;

import javafx.scene.layout.BorderPane;

/**
 * @author david
 */
class ContentsView extends BorderPane {

  private static final int PHOTO_CIRCLE_RADIUS = 48;

  private final NotificationsController controller;

  ContentsView(final NotificationsController controller) {
    this.controller = controller;
    getStyleClass().add("ghn-right-panel");
    initialize();
  }

  private void initialize() {
    // FIXME: contents

  }
}
