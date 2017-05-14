package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import org.controlsfx.control.PopOver;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pt.davidafsilva.ghn.model.filter.post.PostFilter;
import reactor.core.scheduler.Schedulers;

/**
 * @author david
 */
class CreateEditCategoryView extends BorderPane {

  private JFXTextField nameField;
  private FiltersPane filtersPane;

  CreateEditCategoryView(final Predicate<String> categoryWithNameExists,
      final BiConsumer<String, PostFilter> onSave, final Runnable onClose) {
    getStyleClass().add("ghn-create-category-pane");

    // top form
    createForm();

    // bottom buttons
    createButtonsPane(categoryWithNameExists, onSave, onClose);
  }

  private void createForm() {
    final GridPane form = new GridPane();

    // category name
    final Label nameLabel = new Label("Name:");
    nameLabel.setAlignment(Pos.CENTER_RIGHT);
    form.add(nameLabel, 0, 0);
    nameField = new JFXTextField();
    nameLabel.setLabelFor(nameField);
    nameField.setPrefWidth(440);
    nameField.setTooltip(new Tooltip("The name of the category"));
    nameField.setPromptText("<category name>");
    nameField.setFocusColor(Color.rgb(75, 111, 132));
    form.add(nameField, 1, 0);

    // filters
    final Label filtersLabel = new Label("FILTERS");
    nameLabel.setAlignment(Pos.CENTER_RIGHT);
    final HBox filterLabelBox = new HBox();
    filterLabelBox.getStyleClass().add("ghn-create-category-pane-filter-label");
    filterLabelBox.setAlignment(Pos.CENTER);
    filterLabelBox.getChildren().add(filtersLabel);
    form.add(filterLabelBox, 0, 1, 2, 1);
    form.add(filtersPane = new FiltersPane(), 0, 2, 2, 1);

    setTop(form);
  }

  private void createButtonsPane(
      final Predicate<String> categoryWithNameExists,
      final BiConsumer<String, PostFilter> onSave,
      final Runnable onClose) {
    final HBox buttons = new HBox();
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.getStyleClass().add("ghn-create-category-pane-bottom");
    final JFXButton saveBtn = new JFXButton("Save");
    saveBtn.getStyleClass().add("ghn-button");
    saveBtn.setOnMouseClicked(e -> save(categoryWithNameExists, onSave));
    saveBtn.setOnTouchPressed(e -> save(categoryWithNameExists, onSave));
    final JFXButton cancelBtn = new JFXButton("Cancel");
    cancelBtn.getStyleClass().addAll("ghn-button", "ghn-button-exit");
    cancelBtn.setOnMouseClicked(e -> onClose.run());
    cancelBtn.setOnTouchPressed(e -> onClose.run());
    buttons.getChildren().addAll(saveBtn, cancelBtn);
    setBottom(buttons);
  }

  private void save(final Predicate<String> categoryWithNameExists,
      final BiConsumer<String, PostFilter> saveCallback) {
    final String name = nameField.textProperty().getValueSafe().trim();

    // validate name
    if (name.isEmpty()) {
      showError(nameField, "Invalid name");
      nameField.requestFocus();
      return;
    } else if (categoryWithNameExists.test(name)) {
      showError(nameField, "There's already a category with the same name");
      nameField.requestFocus();
      return;
    }

    // get the filter and call the callback
    filtersPane.getCurrentFilter(this::showError)
        .ifPresent(filter -> saveCallback.accept(name, filter));
  }

  private void showError(final Node container, final String message) {
    if (message != null) {
      final HBox content = new HBox();
      final Label label = new Label(message);
      content.getChildren().add(label);
      content.setPadding(new Insets(5));
      content.setAlignment(Pos.CENTER);
      PopOver popOver = new PopOver(content);
      popOver.setFadeInDuration(Duration.millis(500));
      popOver.setFadeOutDuration(Duration.millis(500));
      popOver.setAutoHide(true);
      popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
      Schedulers.timer().schedule(() -> Platform.runLater(() -> popOver.show(container)),
          250, TimeUnit.MILLISECONDS);
      Schedulers.timer().schedule(() -> Platform.runLater(popOver::hide), 5, TimeUnit.SECONDS);
    }
  }
}
