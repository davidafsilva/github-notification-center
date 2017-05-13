package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
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
  private int currentRows;

  FiltersPane() {
    getStyleClass().add("ghn-filter-pane");
    setHgap(5);
    setVgap(5);
    add(createAddFilter(), 0, 0, 5, 1);
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
    final Node addPane = getChildren().remove(getChildren().size() - 1);

    // operator
    final Node operatorChoice = createOperatorChoice();
    operatorChoice.setVisible(currentRows > 0);
    add(operatorChoice, 0, currentRows);

    // filter type
    add(createFilterTypeChoice(), 1, currentRows);

    // textual field
    add(createFilterTextField(), 2, currentRows);

    // delete row
    add(createDeleteFilterButton(), 3, currentRows);

    // increment rows
    currentRows++;

    // add back the add panel
    add(addPane, 0, currentRows, 5, 1);
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

  private Node createDeleteFilterButton() {
    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    final Label deleteLabel = fontAwesome.create(FontAwesome.Glyph.TRASH)
        .size(14);
    deleteLabel.setBorder(Border.EMPTY);
    deleteLabel.setTooltip(new Tooltip("Delete filter"));
    deleteLabel.getStyleClass().add("trash-can");
    deleteLabel.setOnMouseClicked(e -> deleteFilter(getRowIndex(deleteLabel)));
    deleteLabel.setOnMouseClicked(e -> deleteFilter(getRowIndex(deleteLabel)));
    return deleteLabel;
  }

  @SuppressWarnings("unchecked")
  private void deleteFilter(final int rowIndex) {
    final Set<Node> deleteNodes = new HashSet<>();
    for (Node child : getChildren()) {
      // get index from child
      final Integer nodeIndex = GridPane.getRowIndex(child);
      final int idx = nodeIndex == null ? 0 : nodeIndex;

      if (idx > rowIndex) {
        // decrement rows for rows after the deleted row
        GridPane.setRowIndex(child, idx - 1);
      } else if (idx == rowIndex) {
        // collect matching rows for deletion
        deleteNodes.add(child);
      }
    }

    // remove nodes from row
    getChildren().removeAll(deleteNodes);

    // decrement row number
    currentRows--;

    // hide the first operator
    if (rowIndex == 0 && getChildren().size() > 1) {
      // remove the operator from the first (and only) filter
      final JFXComboBox<Label> node = (JFXComboBox<Label>) getChildren().get(0);
      node.setVisible(false);
      node.getSelectionModel().select(0);
    }
  }

  PostFilter getFilter() {
    return filter;
  }
}
