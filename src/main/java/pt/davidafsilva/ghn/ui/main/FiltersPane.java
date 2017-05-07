package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.util.Arrays;
import java.util.List;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import pt.davidafsilva.ghn.model.filter.post.PostFilter;

/**
 * @author david
 */
class FiltersPane extends GridPane {

  private static final List<String> OPERATORS = Arrays.asList(
      "AND", "OR"
  );
  private static final List<String> FILTER_TYPES = Arrays.asList(
      "Repository owner",
      "Repository name",
      "Repository description",
      "Notification title",
      "Notification type"
  );

  private PostFilter filter;

  FiltersPane() {
    getStyleClass().add("ghn-filter-pane");
    setHgap(5);
    setVgap(5);
    add(createAddFilter(), 0, 0, 4, 1);
  }

  private Node createAddFilter() {
    // create category
    final HBox addPane = new HBox();
    addPane.setSpacing(5);
    addPane.setCursor(Cursor.HAND);
    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    final Node plusSign = fontAwesome.create(FontAwesome.Glyph.PLUS_CIRCLE).size(15);
    final Label addLabel = new Label("Add Filter");
    addPane.getChildren().addAll(plusSign, addLabel);
    addPane.setOnMousePressed(e -> appendNewFilterForm());
    addPane.setOnTouchPressed(e -> appendNewFilterForm());
    return addPane;
  }

  private void appendNewFilterForm() {
    final int rowIndex = getChildren().size() - 1;
    final Node addPane = getChildren().remove(rowIndex);

    // operator
    if (rowIndex > 0) {
      add(createOperatorChoice(), 0, rowIndex);
    }

    // filter type
    add(createFilterTypeChoice(), 1, rowIndex);

    // textual field
    add(createFilterTextField(), 2, rowIndex);

    // add back the add panel
    add(addPane, 0, rowIndex + 1, 4, 1);
  }


  private Node createOperatorChoice() {
    final JFXComboBox<Label> operatorsCombo = new JFXComboBox<>();
    operatorsCombo.setPrefWidth(85);
    operatorsCombo.setPromptText("Operator");
    operatorsCombo.setTooltip(new Tooltip("Choose the evaluation operator"));
    OPERATORS.stream()
        .map(Label::new)
        .forEach(operatorsCombo.getItems()::add);
    operatorsCombo.getSelectionModel().select(0);
    return operatorsCombo;
  }

  private Node createFilterTypeChoice() {
    final JFXComboBox<Label> filterTypeCombo = new JFXComboBox<>();
    filterTypeCombo.setPrefWidth(200);
    filterTypeCombo.setPromptText("Type");
    filterTypeCombo.setTooltip(new Tooltip("Choose the desired filter type"));
    FILTER_TYPES.stream()
        .map(Label::new)
        .forEach(filterTypeCombo.getItems()::add);
    return filterTypeCombo;
  }

  private Node createFilterTextField() {
    final JFXTextField textField = new JFXTextField();
    textField.setPromptText("<filter>");
    textField.setTooltip(new Tooltip("Your textual filter"));
    textField.setFocusColor(Color.rgb(75, 111, 132));
    return textField;
  }

  PostFilter getFilter() {
    return filter;
  }
}
