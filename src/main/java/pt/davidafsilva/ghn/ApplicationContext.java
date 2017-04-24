package pt.davidafsilva.ghn;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.service.notification.GitHubNotificationService;
import pt.davidafsilva.ghn.service.auth.GitHubAuthService;
import pt.davidafsilva.ghn.service.options.ApplicationOptionsService;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author david
 */
public class ApplicationContext {

  private final Application application;
  private final Stage primaryStage;
  private final AtomicReference<User> user = new AtomicReference<>();
  private final ApplicationOptionsService applicationOptionsService;
  private final GitHubAuthService gitHubAuthService;
  private final GitHubNotificationService gitHubNotificationService;
  private final Scheduler workScheduler;

  ApplicationContext(final Application application, final Stage primaryStage) {
    this.application = application;
    this.primaryStage = primaryStage;
    this.applicationOptionsService = new ApplicationOptionsService();
    this.gitHubNotificationService = new GitHubNotificationService(this);
    this.gitHubAuthService = new GitHubAuthService(applicationOptionsService.load());
    this.workScheduler = Schedulers.newElastic("ghn");
  }

  public HostServices getHostServices() {
    return application.getHostServices();
  }

  public ApplicationOptions getOptions() {
    return applicationOptionsService.load();
  }

  public User getUser() {
    return user.get();
  }

  public void setUser(final User user) {
    this.user.set(user);
  }

  public GitHubAuthService getGitHubAuthService() {
    return gitHubAuthService;
  }

  public GitHubNotificationService getGitHubNotificationService() {
    return gitHubNotificationService;
  }

  public ApplicationOptionsService getApplicationOptionsService() {
    return applicationOptionsService;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public Scheduler getWorkScheduler() {
    return workScheduler;
  }
}
