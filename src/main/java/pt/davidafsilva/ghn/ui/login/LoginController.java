package pt.davidafsilva.ghn.ui.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.ApplicationController;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.model.mutable.Configuration;
import pt.davidafsilva.ghn.service.GhnException;
import pt.davidafsilva.ghn.service.auth.GitHubAuthService;
import pt.davidafsilva.ghn.service.auth.InvalidCredentialsException;
import pt.davidafsilva.ghn.service.auth.TokenExistsException;
import pt.davidafsilva.ghn.service.auth.TwoFactorAuthRequiredException;
import reactor.core.publisher.Mono;

import static pt.davidafsilva.ghn.util.AuthorizationFacility.isToken;

/**
 * @author david
 */
public class LoginController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

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

  public void autoLogin(final String token) {
    Platform.runLater(loginView::setLoggingIn);
    doLogin(null, token, null, false);
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
      authProcedure = isToken(password) ?
          authService.loginWithToken(password).then(user -> Mono.just(user)
              .doOnNext(u -> saveToken(u.getCredentials()))) :
          createToken(username, password, code).then(authService::loginWithToken);
    }

    // proceed with the login
    loginWith(authProcedure);
  }

  private Mono<String> createToken(final String username, final String password,
      final String code) {
    final GitHubAuthService authService = appContext.getGitHubAuthService();
    return authService.createToken(username, password, code)
        .filter(s -> s != null && !s.isEmpty())
        .doOnError(TokenExistsException.class,
            t -> Platform.runLater(loginView::displayTokenExists))
        .doOnSuccess(token -> {
          LOGGER.info("token was created");
          // save token
          saveToken(token);
        });
  }

  private void loginWith(final Mono<User> authProcedure) {
    authProcedure
        .timeout(Duration.ofSeconds(30))
        .doOnError(TimeoutException.class,
            t -> Platform.runLater(() -> loginView.displayUnexpectedError(
                "Request timed out." + System.lineSeparator() + "Please try again later.")))
        .doOnError(TwoFactorAuthRequiredException.class,
            t -> Platform.runLater(loginView::displayTwoFactorCode))
        .doOnError(InvalidCredentialsException.class,
            t -> Platform.runLater(loginView::displayInvalidCredentials))
        .doOnError(t -> !GhnException.class.isInstance(t),
            t -> Platform.runLater(() -> loginView.displayUnexpectedError(t.getLocalizedMessage())))
        .doOnSuccess(user -> {
          LOGGER.info("successfully logged in as " + user);
          appContext.setUser(user);
          Platform.runLater(() -> {
            loginView.loginSuccessful();
            appController.showMainView();
          });
        })
        .subscribe();
  }

  private Mono<Void> saveToken(final String token) {
    // set the token
    final Configuration configuration = appContext.getConfiguration();
    configuration.getSecuredConfiguration().setToken(token);

    // save the token
    return appContext.getConfigurationService().save(configuration);
  }
}
