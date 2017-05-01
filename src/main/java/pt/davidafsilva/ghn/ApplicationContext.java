package pt.davidafsilva.ghn;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.model.mutable.Configuration;
import pt.davidafsilva.ghn.service.auth.GitHubAuthService;
import pt.davidafsilva.ghn.service.category.CategoryService;
import pt.davidafsilva.ghn.service.configuration.ConfigurationService;
import pt.davidafsilva.ghn.service.notification.GitHubNotificationService;
import pt.davidafsilva.ghn.service.storage.StorageServiceFactory;

/**
 * @author david
 */
public class ApplicationContext {

  private final Application application;
  private final Stage primaryStage;
  private final AtomicReference<User> user = new AtomicReference<>();

  private final ConfigurationService configurationService;
  private final GitHubAuthService gitHubAuthService;
  private final GitHubNotificationService gitHubNotificationService;
  private final CategoryService categoryService;

  ApplicationContext(final Application application, final Stage primaryStage) {
    this.application = application;
    this.primaryStage = primaryStage;

    // initialize services
    final StorageServiceFactory storageServiceFactory = new StorageServiceFactory();
    this.configurationService = new ConfigurationService(
        storageServiceFactory.createUnsecuredStorage(), storageServiceFactory.createSecuredStorage()
    );
    this.gitHubNotificationService = new GitHubNotificationService(this);
    this.gitHubAuthService = new GitHubAuthService(configurationService.load().block());
    this.categoryService = new CategoryService(storageServiceFactory.createUnsecuredStorage());
  }

  public Configuration getConfiguration() {
    // config is already loaded in-memory here
    return configurationService.load().block();
  }

  //-----------
  // App data
  //-----------

  public User getUser() {
    return user.get();
  }

  public void setUser(final User user) {
    this.user.set(user);
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  //-----------
  // services
  //-----------

  public HostServices getHostServices() {
    return application.getHostServices();
  }

  public ConfigurationService getConfigurationService() {
    return configurationService;
  }

  public GitHubAuthService getGitHubAuthService() {
    return gitHubAuthService;
  }

  public GitHubNotificationService getGitHubNotificationService() {
    return gitHubNotificationService;
  }

  public CategoryService getCategoryService() {
    return categoryService;
  }
}
