package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;

import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import pt.davidafsilva.ghn.model.mutable.Category;

/**
 * @author david
 */
class CategoryItem extends BorderPane {

  private static final String _99_PLUS = "99+";
  private Consumer<Category> onDelete = s -> {
  };
  private Consumer<Category> onEdit = s -> {
  };

  private BooleanProperty editableProperty = new SimpleBooleanProperty(false);
  private final JFXBadge badge;
  private final Label trash;
  private final Label edit;

  CategoryItem(final Category category) {
    // style
    getStyleClass().add("ghn-category-item");

    // trash can and edit
    final HBox left = new HBox();
    trash = createTrashCan(category);
    edit = createEdit(category);
    left.setSpacing(3);
    left.getChildren().addAll(trash, edit);

    final Label text = new Label(category.getName());
    text.setMaxWidth(110);

    badge = new JFXBadge(new Label());
    badge.setAlignment(Pos.CENTER_LEFT);
    updateCount(category.getUnreadCount());

    initListeners();

    setLeft(left);
    setAlignment(left, Pos.TOP_LEFT);
    setCenter(text);
    setAlignment(text, Pos.CENTER_LEFT);
    setMargin(text, new Insets(0, 0, 0, 10));
    setRight(badge);
    setAlignment(badge, Pos.CENTER_RIGHT);
  }

  private Label createEdit(final Category category) {
    // trash can
    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    final Label edit = fontAwesome.create(FontAwesome.Glyph.EDIT)
        .size(14);
    edit.setPadding(new Insets(1, 0, 0, 0));
    edit.setBorder(Border.EMPTY);
    edit.setVisible(false);
    edit.setTooltip(new Tooltip("Edit"));
    edit.getStyleClass().add("edit");
    if (category.isEditable()) {
      edit.setOnMousePressed(e -> {
        e.consume();
        onEdit.accept(category);
      });
      edit.setOnTouchPressed(e -> {
        e.consume();
        onEdit.accept(category);
      });
      edit.getStyleClass().add("edit-enabled");
    } else {
      edit.getStyleClass().add("edit-disabled");
    }

    return edit;
  }

  private Label createTrashCan(final Category category) {
    // trash can
    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    final Label trash = fontAwesome.create(FontAwesome.Glyph.TRASH)
        .size(14);
    trash.setBorder(Border.EMPTY);
    trash.setVisible(false);
    trash.setTooltip(new Tooltip("Delete"));
    trash.getStyleClass().add("trash-can");
    if (category.isDeletable()) {
      trash.setOnMousePressed(e -> {
        e.consume();
        confirmDelete(category);
      });
      trash.setOnTouchPressed(e -> {
        e.consume();
        confirmDelete(category);
      });
      trash.getStyleClass().add("trash-can-enabled");
    } else {
      trash.getStyleClass().add("trash-can-disabled");
    }

    return trash;
  }

  private void confirmDelete(final Category category) {
    final PopOver confirmationDialog = new PopOver();

    final BorderPane contents = new DeleteConfirmationPane(category,
        () -> {
          confirmationDialog.hide();
          onDelete.accept(category);
        },
        confirmationDialog::hide);
    confirmationDialog.setContentNode(contents);
    confirmationDialog.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
    confirmationDialog.show(trash);
  }

  private void initListeners() {
    editableProperty.addListener((observable, oldValue, newValue) -> {
      trash.setVisible(newValue);
      edit.setVisible(newValue);
    });
  }

  BooleanProperty editablePropertyProperty() {
    return editableProperty;
  }

  void setEditable(final boolean editable) {
    editableProperty.set(editable);
  }

  void setOnDelete(final Consumer<Category> onDelete) {
    this.onDelete = Objects.requireNonNull(onDelete, "onDelete");
  }

  void setOnEdit(final Consumer<Category> onEdit) {
    this.onEdit = onEdit;
  }

  void updateCount(final int count) {
    if (count > 0) {
      badge.setText(count > 99 ? _99_PLUS : String.valueOf(count));
      badge.setEnabled(true);
    } else {
      badge.setEnabled(false);
    }
    badge.refreshBadge();
  }

  String getText() {
    return ((Label) getCenter()).getText();
  }

  private static class DeleteConfirmationPane extends BorderPane {

    DeleteConfirmationPane(final Category category, final Runnable onYes, final Runnable onNo) {
      getStyleClass().add("ghn-category-delete-pane");

      // top
      final HBox top = new HBox();
      top.getStyleClass().add("ghn-category-delete-pane-top");
      final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
      final Node questionMark = fontAwesome.create(FontAwesome.Glyph.QUESTION_CIRCLE)
          .size(14);
      top.getChildren().add(questionMark);
      final Label topLabel = new Label("Delete Confirmation");
      top.getChildren().add(topLabel);
      setTop(top);

      // center
      final HBox center = new HBox();
      center.getStyleClass().add("ghn-category-delete-pane-center");
      final Label categoryName = new Label(category.getName());
      categoryName.getStyleClass().add("ghn-bold");
      final TextFlow centerLabel = new TextFlow(
          new Label("Are you sure you want to delete the category \""),
          categoryName, new Label("\" ?"));
      center.getChildren().add(centerLabel);
      setCenter(center);

      // buttons
      final HBox buttons = new HBox();
      buttons.getStyleClass().add("ghn-category-delete-pane-bottom");
      final JFXButton yesBtn = new JFXButton("Yes");
      yesBtn.getStyleClass().add("ghn-button");
      yesBtn.setOnMouseClicked(e -> onYes.run());
      final JFXButton noBtn = new JFXButton("No");
      noBtn.getStyleClass().addAll("ghn-button", "ghn-button-exit");
      noBtn.setOnMouseClicked(e -> onNo.run());
      buttons.getChildren().addAll(yesBtn, noBtn);
      setBottom(buttons);
    }
  }
}
