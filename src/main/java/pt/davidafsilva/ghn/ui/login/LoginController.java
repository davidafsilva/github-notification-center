package pt.davidafsilva.ghn.ui.login;

import java.time.Duration;

import javafx.application.Platform;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.ApplicationController;
import pt.davidafsilva.ghn.service.GhnException;
import pt.davidafsilva.ghn.service.GitHubAuthService;
import pt.davidafsilva.ghn.service.InvalidCredentialsException;
import pt.davidafsilva.ghn.service.TwoFactorAuthRequiredException;

/**
 * @author david
 */
public class LoginController {

  private final ApplicationController appController;
  private final ApplicationContext appContext;
  private final LoginView loginView;

  public LoginController(final ApplicationController appController) {
    this.appController = appController;
    this.appContext = appController.getApplicationContext();
    this.loginView = new LoginView(
        this::doLogin,
        appController::exit
    );
  }

  public LoginView initView() {
    // create the login view
    loginView.initView();
    return loginView;
  }

  private void doLogin(final String username, final String password, final String code) {
    final GitHubAuthService authService = appContext.getGitHubAuthService();
    authService.loginWithPassword(username, password, code)
        .timeout(Duration.ofSeconds(15))
        .log()
        .doOnError(TwoFactorAuthRequiredException.class,
            t -> Platform.runLater(loginView::displayTwoFactorDialog))
        .doOnError(InvalidCredentialsException.class,
            t -> Platform.runLater(loginView::displayInvalidCredentials))
        .doOnError(t -> !GhnException.class.isInstance(t),
            t -> Platform.runLater(() -> loginView.displayUnexpectedError(t.getLocalizedMessage())))
        .doOnSuccess(user -> {
          System.out.println(user);
          appContext.setUser(user);
          Platform.runLater(() -> {
            loginView.loginSuccessful();
            appController.showMainView();
          });
        })
        .subscribeOn(appContext.getWorkScheduler())
        .subscribe();
  }
}
