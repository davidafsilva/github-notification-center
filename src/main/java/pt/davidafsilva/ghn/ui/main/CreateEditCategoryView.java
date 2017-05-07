package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import java.util.function.BiConsumer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import pt.davidafsilva.ghn.model.filter.post.PostFilter;

/**
 * @author david
 */
class CreateEditCategoryView extends BorderPane {

  private JFXTextField nameField;
  private FiltersPane filtersPane;

  CreateEditCategoryView(final BiConsumer<String, PostFilter> onSave, final Runnable onClose) {
    getStyleClass().add("ghn-create-category-pane");

    // top form
    createForm();

    // bottom buttons
    createButtonsPane(onSave, onClose);
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

  private void createButtonsPane(final BiConsumer<String, PostFilter> onSave, final Runnable onClose) {
    final HBox buttons = new HBox();
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.getStyleClass().add("ghn-create-category-pane-bottom");
    final JFXButton saveBtn = new JFXButton("Save");
    saveBtn.getStyleClass().add("ghn-button");
    saveBtn.setOnMouseClicked(e -> onSave.accept(nameField.getText(), filtersPane.getFilter()));
    final JFXButton cancelBtn = new JFXButton("Cancel");
    cancelBtn.getStyleClass().addAll("ghn-button", "ghn-button-exit");
    cancelBtn.setOnMouseClicked(e -> onClose.run());
    buttons.getChildren().addAll(saveBtn, cancelBtn);
    setBottom(buttons);
  }
}
