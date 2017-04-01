package pt.davidafsilva.ghn;

import javax.swing.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.davidafsilva.ghn.ui.login.LoginController;
import pt.davidafsilva.ghn.ui.login.LoginView;

/**
 * @author david
 */
public class ApplicationController {

  private final ApplicationContext ctx;

  private ApplicationController(final Application application, final Stage primaryStage) {
    this.ctx = new ApplicationContext(application, primaryStage);
  }

  public ApplicationContext getApplicationContext() {
    return ctx;
  }

  static void start(final Application application, final Stage primaryStage) {
    primaryStage.setTitle("GitHub Notification Center");
    primaryStage.initStyle(StageStyle.UTILITY);
    primaryStage.setOnCloseRequest(e -> {
      Platform.exit();
      System.exit(0);
    });
    setIcons(primaryStage);

    final ApplicationController appController = new ApplicationController(application, primaryStage);
    appController.showLoginView(primaryStage);
  }

  private static void setIcons(final Stage primaryStage) {
    final Image icon = new Image(ApplicationController.class
        .getResourceAsStream("/github-icon.png"));
    primaryStage.getIcons().add(icon);

    // OS X dock icon
    try {
      Class.forName("com.apple.eawt.Application");
      com.apple.eawt.Application.getApplication().setDockIconImage(
          new ImageIcon(ApplicationController.class.getResource("/github-icon.png")).getImage());
    } catch (ClassNotFoundException e) {
      // not applicable to windows/linux
    }
  }

  private void showLoginView(final Stage stage) {
    // create the login view
    final LoginController loginController = new LoginController(this);
    final LoginView loginView = loginController.initView();

    final Scene scene = new Scene(loginView, 300, 350);
    scene.getStylesheets().add(ApplicationController.class.getResource("/app.css")
        .toExternalForm());
    stage.setScene(scene);
    stage.show();
  }

  public void showMainView() {
    // TODO: change me - auto-generated block

  }

  public void exit() {
    Platform.runLater(() -> {
      ctx.getWorkScheduler().dispose();
      ctx.getPrimaryStage().close();
      System.exit(0);
    });
  }
}
