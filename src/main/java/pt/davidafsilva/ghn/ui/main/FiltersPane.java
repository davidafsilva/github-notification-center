package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import pt.davidafsilva.ghn.model.filter.post.PostFilter;
import pt.davidafsilva.ghn.model.filter.post.PostFilterBuilder;
import pt.davidafsilva.ghn.model.filter.post.PostFilterBuilder.FilterOperator;
import pt.davidafsilva.ghn.model.filter.post.StringFilter;
import pt.davidafsilva.ghn.util.Consumer3;

/**
 * @author david
 */
class FiltersPane extends GridPane {

  private static final Map<String, PostFilterBuilder.FilterOperator> OPERATORS = new TreeMap<>();

  static {
    OPERATORS.put("AND", PostFilter::and);
    OPERATORS.put("OR", PostFilter::or);
  }

  private static final List<FilterType> FILTER_TYPES = Arrays.asList(
      FilterType.REPO_OWNER, FilterType.REPO_NAME, FilterType.REPO_DESC,
      FilterType.NOTIF_TITLE, FilterType.NOTIF_TYPE
  );

  private static final List<StringFnType> STR_MATCH_FUNCTIONS = Arrays.asList(
      StringFnType.CONTAINS, StringFnType.STARTS_WITH, StringFnType.ENDS_WITH, StringFnType.PATTERN
  );

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

    // string match type field
    add(createFilterTextMatchTypeChoice(), 2, currentRows);

    // textual field
    add(createFilterTextField(), 3, currentRows);

    // delete row
    add(createDeleteFilterButton(), 4, currentRows);

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
    OPERATORS.keySet().stream()
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
        .map(ft -> new Label(ft.description))
        .forEach(filterTypeCombo.getItems()::add);
    return filterTypeCombo;
  }

  private Node createFilterTextMatchTypeChoice() {
    final JFXComboBox<Label> filterTypeCombo = new JFXComboBox<>();
    filterTypeCombo.setPrefWidth(150);
    filterTypeCombo.setPromptText("Match");
    filterTypeCombo.setTooltip(new Tooltip("Choose the desired string matching function"));
    STR_MATCH_FUNCTIONS.stream()
        .map(ft -> new Label(ft.description))
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
    // relocate and collect for deletion
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

  Optional<PostFilter> getCurrentFilter(final Consumer<Node> markAsRequired) {
    final PostFilterBuilder builder = new PostFilterBuilder();
    final Map<Node, List<String>> errors = new LinkedHashMap<>();
    for (int row = 0; row < currentRows; row++) {
      buildRow(row, builder, errors);
    }

    // show errors
    if (!errors.isEmpty()) {
      errors.forEach((n, e) -> markAsRequired.accept(n));
      return Optional.empty();
    }

    return Optional.of(builder.build());
  }

  private void buildRow(final int row, final PostFilterBuilder builder,
      final Map<Node, List<String>> errors) {
    final int offset = row * 5;

    // get the fields
    final Node operatorField = getChildren().get(offset);
    final PostFilterBuilder.FilterOperator operator = getOperator(operatorField);
    final Node filterTypeField = getChildren().get(offset + 1);
    final FilterType filterType = getFilterType(filterTypeField);
    final Node strFnTypeField = getChildren().get(offset + 2);
    final StringFnType strFnType = getStringFnType(strFnTypeField);
    final Node textField = getChildren().get(offset + 3);
    final String textFilter = getTextFilter(textField);

    // validate
    if (operator == null) {
      errors.computeIfAbsent(operatorField, k -> new ArrayList<>())
          .add("Please choose a valid operator");
    }
    if (filterType == null) {
      errors.computeIfAbsent(filterTypeField, k -> new ArrayList<>())
          .add("Please choose a valid filter type");
    }
    if (strFnType == null) {
      errors.computeIfAbsent(strFnTypeField, k -> new ArrayList<>())
          .add("Please choose a valid string function");
    }

    // add the filter
    if (errors.isEmpty()) {
      assert strFnType != null;
      assert filterType != null;
      final StringFilter strFilter = strFnType.mapper.apply(textFilter);
      filterType.mapper.accept(builder, operator, strFilter);
    }
  }

  @SuppressWarnings("unchecked")
  private PostFilterBuilder.FilterOperator getOperator(final Node node) {
    final JFXComboBox<Label> combo = (JFXComboBox<Label>) node;
    final Label selected = combo.getSelectionModel().getSelectedItem();
    return selected == null ? null : OPERATORS.get(selected.getText());
  }

  @SuppressWarnings("unchecked")
  private FilterType getFilterType(final Node node) {
    final JFXComboBox<Label> combo = (JFXComboBox<Label>) node;
    final int selected = combo.getSelectionModel().getSelectedIndex();
    return selected < 0 ? null : FILTER_TYPES.get(selected);
  }

  @SuppressWarnings("unchecked")
  private StringFnType getStringFnType(final Node node) {
    final JFXComboBox<Label> combo = (JFXComboBox<Label>) node;
    final int selected = combo.getSelectionModel().getSelectedIndex();
    return selected < 0 ? null : STR_MATCH_FUNCTIONS.get(selected);
  }

  private String getTextFilter(final Node node) {
    final JFXTextField textField = (JFXTextField) node;
    return textField.textProperty().getValueSafe();
  }

  private enum FilterType {
    REPO_OWNER("Repository owner", PostFilterBuilder::repoOwner),
    REPO_NAME("Repository name", PostFilterBuilder::repoName),
    REPO_DESC("Repository description", PostFilterBuilder::repoDescription),
    NOTIF_TITLE("Notification title", PostFilterBuilder::title),
    NOTIF_TYPE("Notification type", PostFilterBuilder::type);

    private final String description;
    private final Consumer3<PostFilterBuilder, FilterOperator, StringFilter> mapper;

    FilterType(final String description,
        final Consumer3<PostFilterBuilder, FilterOperator, StringFilter> mapper) {
      this.description = description;
      this.mapper = mapper;
    }
  }

  private enum StringFnType {
    CONTAINS("Contains", StringFilter::contains),
    STARTS_WITH("Starts with", StringFilter::startsWith),
    ENDS_WITH("Ends with", StringFilter::endsWith),
    PATTERN("Pattern", StringFilter::regex);

    private final String description;
    private final Function<String , StringFilter> mapper;

    StringFnType(final String description,
        final Function<String, StringFilter> mapper) {
      this.description = description;
      this.mapper = mapper;
    }
  }
}
