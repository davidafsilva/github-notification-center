package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * @author david
 */
public class NotificationsView extends JFXDrawer {

  private static final int PHOTO_CIRCLE_RADIUS = 48;

  private final NotificationsController controller;

  NotificationsView(final NotificationsController controller) {
    this.controller = controller;
  }

  void initView() {
    setOverLayVisible(false);
    setResizableOnDrag(false);

    // left side
    initLeftPanel();

    // right side
    initRightPanel();
  }

  private void toggleDrawer() {
    if (isShown()) {
      close();
    } else {
      open();
    }
  }

  private void initLeftPanel() {
    final VBox panel = new VBox();
    panel.setPadding(new Insets(15));
    panel.setSpacing(20);

    // user & photo panel
    final VBox photoUserPanel = new VBox();

    // photo
    photoUserPanel.setFillWidth(true);
    photoUserPanel.setSpacing(10);
    final ImageView photoImage = new ImageView(loadImage(getDefaultAvatar(), false));
    photoImage.setClip(new Circle(PHOTO_CIRCLE_RADIUS, PHOTO_CIRCLE_RADIUS, PHOTO_CIRCLE_RADIUS));
    final Label photo = new Label("", photoImage);
    photo.setAlignment(Pos.CENTER);
    photo.setMaxWidth(Double.MAX_VALUE);
    photoUserPanel.getChildren().add(photo);
    // lazily load the actual image from the network
    controller.getUser().getAvatarUrl().ifPresent(url -> {
      final Image img = loadImage(url, true);
      //img.progressProperty().isEqualTo(1).addListener((a, b, c) -> photoImage.setImage(img));
    });

    // username
    final Label username = new Label(controller.getUser().getUsername());
    username.setAlignment(Pos.CENTER);
    username.setMaxWidth(Double.MAX_VALUE);
    username.getStyleClass().add("ghn-left-panel-username");
    photoUserPanel.getChildren().add(username);

    // add photo & username panel
    panel.getChildren().add(photoUserPanel);

    // hamburgers to aid close/open drawer
    final JFXHamburger hamburger = new JFXHamburger();
    StackPane.setAlignment(hamburger, Pos.TOP_RIGHT);
    final HamburgerBackArrowBasicTransition bt = new HamburgerBackArrowBasicTransition(hamburger);
    bt.setRate(0.75);
    hamburger.setOnMouseClicked(e -> {
      bt.setRate(bt.getRate() * -1);
      bt.play();
      toggleDrawer();
    });

    // set the contents
    setSidePane(panel, hamburger);

    // open the drawer by default
    bt.play();
    open();
  }

  private Image loadImage(final String path, final boolean inBackground) {
    return new Image(path, PHOTO_CIRCLE_RADIUS * 2,
        PHOTO_CIRCLE_RADIUS * 2, true, true, inBackground);
  }

  private String getDefaultAvatar() {
    return getClass().getResource("/ghnc-96.png").toExternalForm();
  }

  private void initRightPanel() {
    final VBox panel = new VBox();
    panel.setPadding(new Insets(15));
    panel.setMinWidth(400);
    panel.setMaxWidth(Double.MAX_VALUE);
    setContent(panel);
  }
}
