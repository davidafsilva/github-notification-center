package pt.davidafsilva.ghn.ui.main;

import java.util.logging.Logger;

import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.ApplicationController;
import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.model.User;

/**
 * @author david
 */
public class NotificationsController {

  private static final Logger LOGGER = Logger.getLogger(NotificationsController.class.getName());
  private static final String GITHUB_REPO =
      "https://github.com/davidafsilva/github-notification-center";
  private static final String DAVIDAFSILVA_PT = "http://davidafsilva.pt";

  private final ApplicationController appController;
  private final ApplicationContext appContext;
  private final NotificationsView notificationsView;

  public NotificationsController(final ApplicationController appController) {
    this.appController = appController;
    this.appContext = appController.getApplicationContext();
    this.notificationsView = new NotificationsView(this);
  }

  public NotificationsView initView() {
    // create the view
    notificationsView.initView();
    return notificationsView;
  }

  ApplicationOptions getOptions() {
    return appContext.getOptions();
  }

  User getUser() {
    return appContext.getUser();
  }

  public void loadNotifications() {
    // TODO: change me - auto-generated block

  }

  void openGitHub() {
    appContext.getHostServices().showDocument(GITHUB_REPO);
  }

  void openWebPage() {
    appContext.getHostServices().showDocument(DAVIDAFSILVA_PT);
  }
}
