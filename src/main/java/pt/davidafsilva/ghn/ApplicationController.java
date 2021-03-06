package pt.davidafsilva.ghn;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.ui.login.LoginController;
import pt.davidafsilva.ghn.ui.login.LoginView;
import pt.davidafsilva.ghn.ui.main.NotificationsController;
import pt.davidafsilva.ghn.ui.main.NotificationsView;
import pt.davidafsilva.ghn.util.Schedulers;

/**
 * @author david
 */
public class ApplicationController {

  private static final AtomicReference<ApplicationController> CONTROLLER = new AtomicReference<>();

  private final ApplicationContext ctx;

  private ApplicationController(final Application application, final Stage primaryStage) {
    this.ctx = new ApplicationContext(application, primaryStage);
  }

  public ApplicationContext getApplicationContext() {
    return ctx;
  }

  static void start(final Application application, final Stage primaryStage) {
    primaryStage.setTitle("GitHub Notification Center");
    primaryStage.getIcons().add(new Image(ApplicationController.class
        .getResourceAsStream("/ghnc-64.png")));

    final ApplicationController appController = new ApplicationController(application,
        primaryStage);
    CONTROLLER.set(appController);
    //appController.showLoginView(primaryStage);

    appController.ctx.setUser(
        new User("davidafsilva", "xxx", "https://avatars1.githubusercontent.com/u/2266642?v=3"));
    appController.showMainView();
  }

  static void stop() {
    CONTROLLER.get().exit();
  }

  private void showLoginView(final Stage stage) {
    // create the login view
    final LoginController loginController = new LoginController(this);
    final LoginView loginView = loginController.initView();

    final Scene scene = new Scene(loginView, 300, 350);
    scene.getStylesheets().add(ApplicationController.class.getResource("/app.css")
        .toExternalForm());
    stage.setScene(scene);
    stage.setOnShown(event -> ctx.getConfiguration().getSecuredConfiguration()
        .getToken().ifPresent(loginController::autoLogin));
    stage.show();
  }

  public void showMainView() {
    final Stage stage = ctx.getPrimaryStage();

    // close login view
    stage.close();

    // create notifications view
    final NotificationsController controller = new NotificationsController(this);
    final NotificationsView view = controller.initView();

    // open main view
    final Rectangle2D screenResolution = Screen.getPrimary().getVisualBounds();
    final double width = Math.max(screenResolution.getWidth() * .65, 600);
    final double height = Math.max(screenResolution.getHeight() * .70, 400);
    final Scene scene = new Scene(view, width, height);
    scene.getStylesheets().add(ApplicationController.class.getResource("/app.css")
        .toExternalForm());
    stage.setMinWidth(650);
    stage.setWidth(width);
    stage.setMinHeight(400);
    stage.setHeight(height);
    stage.setScene(scene);
    stage.setOnShown(e -> {
      // load categories
      controller.loadCategories();
      //  load notifications
      controller.loadNotifications();
    });
    stage.show();
  }

  public void exit() {
    Schedulers.dispose();
    Platform.exit();
  }
}
