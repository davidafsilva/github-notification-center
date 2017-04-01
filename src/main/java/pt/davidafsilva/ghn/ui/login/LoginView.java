package pt.davidafsilva.ghn.ui.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import org.controlsfx.control.PopOver;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
import pt.davidafsilva.ghn.util.Consumer4;
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
  private JFXCheckBox rememberCheckBox;
  private GridPane loginForm;

  // callbacks
  private final Consumer4<String, String, String, Boolean> onLogin;
  private final Runnable onCancel;
  private final Consumer<String> urlOpener;

  LoginView(final Consumer4<String, String, String, Boolean> onLogin,
      final Runnable onCancel, final Consumer<String> urlOpener) {
    this.onLogin = onLogin;
    this.onCancel = onCancel;
    this.urlOpener = urlOpener;
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

    rememberCheckBox = new JFXCheckBox("Remember?");
    rememberCheckBox.setTooltip(new Tooltip("Creates an Application Token and remembers it"));
    loginForm.add(rememberCheckBox, 1, 2);

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
        doLogin(userField.getText(), passwordField.getText(), twoFactorField.getText(),
            rememberCheckBox.isSelected());
      }
    };
    userField.setOnKeyReleased(enterEventHandler);
    passwordField.setOnKeyReleased(enterEventHandler);
    twoFactorField.setOnKeyReleased(enterEventHandler);
    loginButton.setOnAction(
        event -> doLogin(userField.getText(), passwordField.getText(), twoFactorField.getText(),
            rememberCheckBox.isSelected()));
  }

  private void doLogin(final String user, final String password, final String code,
      final boolean createToken) {
    // disable UI
    progressSpinner.setVisible(true);
    setDisabled(true);

    // call the login procedure
    onLogin.accept(user, password, code, createToken);
  }

  void displayTwoFactorCode() {
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
    showTooltip(twoFactorField, "2-Factor Authentication Code Required", () -> {});
  }

  void displayInvalidCredentials() {
    displayErrorMessage("Invalid credentials", () -> {
    });
    passwordField.requestFocus();
  }

  void displayUnexpectedError(final String message) {
    displayErrorMessage(message, () -> {
    });
  }

  void displayTokenExists() {
    displayErrorMessage("There's already an existent token." + System.lineSeparator() +
            "Please delete it and login again." + System.lineSeparator() + System.lineSeparator() +
            "Go to a https://github.com/settings/tokens",
        () -> urlOpener.accept("https://github.com/settings/tokens"));
  }

  private void displayErrorMessage(final String message, final Runnable onClick) {
    twoFactorField.clear();
    progressSpinner.setVisible(false);
    setDisabled(false);
    showTooltip(passwordField, message, onClick);
  }

  private void showTooltip(final Node container, final String message, final Runnable onClick) {
    if (message != null && !message.isEmpty()) {
      final HBox content = new HBox();
      final Label label = new Label(message);
      content.getChildren().add(label);
      content.setPadding(new Insets(5));
      content.setAlignment(Pos.CENTER);
      content.setOnMouseReleased(e -> onClick.run());
      content.setOnTouchReleased(e -> onClick.run());
      PopOver popOver = new PopOver(content);
      popOver.setFadeInDuration(Duration.millis(500));
      popOver.setFadeOutDuration(Duration.millis(500));
      popOver.setAutoHide(true);
      Schedulers.timer().schedule(() -> Platform.runLater(() -> popOver.show(container)),
          250, TimeUnit.MILLISECONDS);
      Schedulers.timer().schedule(() -> Platform.runLater(popOver::hide), 5, TimeUnit.SECONDS);
    }
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
