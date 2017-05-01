package pt.davidafsilva.ghn;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.model.mutable.Configuration;
import pt.davidafsilva.ghn.service.auth.GitHubAuthService;
import pt.davidafsilva.ghn.service.configuration.ApplicationConfigurationService;
import pt.davidafsilva.ghn.service.notification.GitHubNotificationService;
import pt.davidafsilva.ghn.service.storage.StorageServiceFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author david
 */
public class ApplicationContext {

  private final Application application;
  private final Stage primaryStage;
  private final AtomicReference<User> user = new AtomicReference<>();
  private final ApplicationConfigurationService applicationConfigurationService;
  private final GitHubAuthService gitHubAuthService;
  private final GitHubNotificationService gitHubNotificationService;
  private final Scheduler workScheduler;

  ApplicationContext(final Application application, final Stage primaryStage) {
    this.application = application;
    this.primaryStage = primaryStage;

    // initialize services
    final StorageServiceFactory storageServiceFactory = new StorageServiceFactory();
    this.applicationConfigurationService = new ApplicationConfigurationService(
        storageServiceFactory.createUnsecuredStorage(), storageServiceFactory.createSecuredStorage()
    );
    this.gitHubNotificationService = new GitHubNotificationService(this);
    this.gitHubAuthService = new GitHubAuthService(applicationConfigurationService.load().block());
    this.workScheduler = Schedulers.newElastic("ghn");
  }

  public HostServices getHostServices() {
    return application.getHostServices();
  }


  public ApplicationConfigurationService getConfigurationService() {
    return applicationConfigurationService;
  }

  public Configuration getConfiguration() {
    // config is already loaded in-memory here
    return applicationConfigurationService.load().block();
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

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public Scheduler getWorkScheduler() {
    return workScheduler;
  }
}
