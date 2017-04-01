package pt.davidafsilva.ghn.ui.login;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.ApplicationController;
import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.service.GhnException;
import pt.davidafsilva.ghn.service.auth.GitHubAuthService;
import pt.davidafsilva.ghn.service.auth.InvalidCredentialsException;
import pt.davidafsilva.ghn.service.auth.TokenExistsException;
import pt.davidafsilva.ghn.service.auth.TwoFactorAuthRequiredException;
import reactor.core.publisher.Mono;

/**
 * @author david
 */
public class LoginController {

  private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

  private final ApplicationController appController;
  private final ApplicationContext appContext;
  private final LoginView loginView;

  public LoginController(final ApplicationController appController) {
    this.appController = appController;
    this.appContext = appController.getApplicationContext();
    this.loginView = new LoginView(
        this::doLogin,
        appController::exit,
        appContext.getHostServices()::showDocument);
  }

  public LoginView initView() {
    // create the login view
    loginView.initView();
    return loginView;
  }

  private void doLogin(final String username, final String password, final String code,
      final boolean createToken) {
    final GitHubAuthService authService = appContext.getGitHubAuthService();
    final Mono<User> authProcedure;
    if (!createToken) {
      // do the login -
      authProcedure = isToken(password) ?
          authService.loginWithToken(password) :
          authService.loginWithPassword(username, password, code);
    } else {
      // create the token and login
      authProcedure = createToken(username, password, code)
          .then(authService::loginWithToken);
    }

    // proceed with the login
    loginWith(authProcedure);
  }

  private boolean isToken(final String credential) {
    return credential.length() == 40;
  }

  private Mono<String> createToken(final String username, final String password,
      final String code) {
    final GitHubAuthService authService = appContext.getGitHubAuthService();
    return authService.createToken(username, password, code)
        .filter(s -> s != null && !s.isEmpty())
        .doOnError(TokenExistsException.class,
            t -> Platform.runLater(loginView::displayTokenExists))
        .doOnSuccess(token -> {
          LOGGER.log(Level.INFO, "token was created");
          // save token
          final ApplicationOptions options = appContext.getOptions();
          options.setToken(token);
          appContext.getApplicationOptionsService().save(options);
        });
  }

  private void loginWith(final Mono<User> authProcedure) {
    authProcedure
        .timeout(Duration.ofSeconds(15))
        .log()
        .doOnError(TwoFactorAuthRequiredException.class,
            t -> Platform.runLater(loginView::displayTwoFactorCode))
        .doOnError(InvalidCredentialsException.class,
            t -> Platform.runLater(loginView::displayInvalidCredentials))
        .doOnError(t -> !GhnException.class.isInstance(t),
            t -> Platform.runLater(() -> loginView.displayUnexpectedError(t.getLocalizedMessage())))
        .doOnSuccess(user -> {
          LOGGER.log(Level.INFO, "successfully logged in as " + user);
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
