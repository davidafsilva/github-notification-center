package pt.davidafsilva.ghn;

import java.util.concurrent.atomic.AtomicReference;

import javafx.stage.Stage;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.service.GitHubAuthService;
import pt.davidafsilva.ghn.service.GitHubService;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author david
 */
public class ApplicationContext {

  private final Stage primaryStage;
  private final ApplicationOptions options;
  private final AtomicReference<User> user = new AtomicReference<>();
  private final GitHubAuthService gitHubAuthService;
  private final GitHubService gitHubService;
  private final Scheduler workScheduler;

  ApplicationContext(final Stage primaryStage, final ApplicationOptions options) {
    this.primaryStage = primaryStage;
    this.options = options;
    this.gitHubAuthService = new GitHubAuthService(options);
    this.gitHubService = new GitHubService(this);
    this.workScheduler = Schedulers.newElastic("ghn");
  }

  public ApplicationOptions getOptions() {
    return options;
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

  public GitHubService getGitHubService() {
    return gitHubService;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public Scheduler getWorkScheduler() {
    return workScheduler;
  }
}
