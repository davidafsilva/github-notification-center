package pt.davidafsilva.ghn.ui.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import org.controlsfx.control.PopOver;

import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pt.davidafsilva.ghn.util.Consumer3;
import reactor.core.scheduler.Schedulers;

/**
 * @author david
 */
public class LoginView extends GridPane {

  // UI controls
  private Label twoFactorCodeLabel;
  private JFXSpinner progressSpinner;
  private JFXTextField userField, twoFactorField;
  private JFXPasswordField passwordField;
  private GridPane loginForm;

  // callbacks
  private final Consumer3<String, String, String> onLogin;
  private final Runnable onCancel;

  LoginView(final Consumer3<String, String, String> onLogin,
      final Runnable onCancel) {
    this.onLogin = onLogin;
    this.onCancel = onCancel;
  }

  void initView() {
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(15, 15, 15, 15));

    // logo
    final Label logoLabel = new Label("", new ImageView(
        new Image(getClass().getResourceAsStream("/github-icon-128x128.png"))));
    final HBox logoContainer = new HBox(logoLabel);
    logoContainer.setAlignment(Pos.CENTER);
    add(logoContainer, 1, 0, 3, 1);

    // login form
    loginForm = new GridPane();
    final Label userLabel = new Label("Username:");
    userLabel.setAlignment(Pos.CENTER_RIGHT);
    loginForm.add(userLabel, 0, 0);
    userField = new JFXTextField();
    userLabel.setLabelFor(userField);
    userField.setTooltip(new Tooltip("Your GitHub username"));
    userField.setPromptText("<username>");
    userField.setFocusColor(Color.rgb(75, 111, 132));
    loginForm.add(userField, 1, 0);

    final Label pwdLabel = new Label("Password:");
    pwdLabel.setAlignment(Pos.CENTER_RIGHT);
    loginForm.add(pwdLabel, 0, 1);
    passwordField = new JFXPasswordField();
    pwdLabel.setLabelFor(passwordField);
    passwordField.setFocusColor(Color.rgb(75, 111, 132));
    passwordField.setPromptText("<password>");
    passwordField.setTooltip(new Tooltip("Your GitHub password"));
    loginForm.add(passwordField, 1, 1);

    twoFactorCodeLabel = new Label("2F Code:");
    twoFactorCodeLabel.setVisible(false);
    twoFactorCodeLabel.setDisable(true);
    twoFactorCodeLabel.setAlignment(Pos.CENTER_RIGHT);
    twoFactorField = new JFXTextField();
    twoFactorCodeLabel.setLabelFor(twoFactorField);
    twoFactorField.setVisible(false);
    twoFactorField.setDisable(true);
    twoFactorField.setFocusColor(Color.rgb(75, 111, 132));
    twoFactorField.setPromptText("<optional 2F code>");
    twoFactorField.setTooltip(new Tooltip("2-Factor authentication code"));
    add(loginForm, 1, 1);

    // add buttons
    final HBox buttons = new HBox();
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.setSpacing(5);
    final Button loginButton = new JFXButton("Login");
    loginButton.getStyleClass().add("ghn-button");
    final Button cancelButton = new JFXButton("Exit");
    cancelButton.setOnAction(e -> onCancel.run());
    cancelButton.getStyleClass().add("ghn-button-exit");
    progressSpinner = new JFXSpinner();
    progressSpinner.setVisible(false);
    buttons.getChildren().add(progressSpinner);
    buttons.getChildren().add(loginButton);
    buttons.getChildren().add(cancelButton);
    add(buttons, 1, 3);

    // add event listeners
    final EventHandler<KeyEvent> enterEventHandler = ke -> {
      if (ke.getCode() == KeyCode.ENTER) {
        doLogin(userField.getText(), passwordField.getText(), twoFactorField.getText());
      }
    };
    userField.setOnKeyReleased(enterEventHandler);
    passwordField.setOnKeyReleased(enterEventHandler);
    twoFactorField.setOnKeyReleased(enterEventHandler);
    loginButton.setOnAction(
        event -> doLogin(userField.getText(), passwordField.getText(), twoFactorField.getText()));
  }

  private void doLogin(final String user, final String password, final String code) {
    // disable UI
    progressSpinner.setVisible(true);
    setDisabled(true);

    // call the login procedure
    onLogin.accept(user, password, code);
  }

  void displayTwoFactorDialog() {
    twoFactorCodeLabel.setVisible(true);
    twoFactorCodeLabel.setDisable(false);
    twoFactorField.setVisible(true);
    twoFactorField.setDisable(false);
    twoFactorField.clear();

    userField.setDisable(true);
    passwordField.setDisable(true);

    if (twoFactorField.getParent() == null) {
      loginForm.add(twoFactorCodeLabel, 0, 3);
      loginForm.add(twoFactorField, 1, 3);
    }

    progressSpinner.setVisible(false);
    setDisabled(false);
    twoFactorField.requestFocus();
    Platform.runLater(() -> showTooltip(twoFactorField, "2-Factor Authentication Code Required"));
  }

  void displayInvalidCredentials() {
    displayUnexpectedError("Invalid credentials");
    passwordField.requestFocus();
  }

  void displayUnexpectedError(final String message) {
    twoFactorField.clear();
    progressSpinner.setVisible(false);
    setDisabled(false);
    showTooltip(passwordField, message);
  }

  private void showTooltip(final Node container, final String message) {
    final HBox content = new HBox();
    content.getChildren().add(new Label(message));
    content.setPadding(new Insets(5));
    content.setAlignment(Pos.CENTER);
    PopOver popOver = new PopOver(content);
    popOver.setFadeInDuration(Duration.millis(500));
    popOver.setFadeOutDuration(Duration.millis(500));
    popOver.setAutoHide(true);
    Platform.runLater(() -> popOver.show(container));
    Schedulers.timer().schedule(() -> Platform.runLater(popOver::hide), 5, TimeUnit.SECONDS);
  }

  void loginSuccessful() {
    userField.setDisable(false);
    userField.clear();
    passwordField.setDisable(false);
    passwordField.clear();
    twoFactorField.setDisable(false);
    twoFactorField.clear();
    if (twoFactorField.getParent() != null) {
      loginForm.getChildren().remove(twoFactorCodeLabel);
      loginForm.getChildren().remove(twoFactorField);
    }

    progressSpinner.setVisible(false);
    setDisabled(false);
  }
}
