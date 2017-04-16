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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;

/**
 * @author david
 */
class CategoryItem extends BorderPane {

  private static final String _99_PLUS = "99+";
  private Consumer<String> onDelete = s -> {
  };

  private BooleanProperty editableProperty = new SimpleBooleanProperty(false);
  private final JFXBadge badge;
  private final Label trash;

  CategoryItem(final String name, final int count) {
    // style
    getStyleClass().add("ghn-category-item");

    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    trash = fontAwesome.create(FontAwesome.Glyph.TRASH)
        .size(14);
    trash.setBorder(Border.EMPTY);
    trash.setOnMousePressed(e -> {
      e.consume();
      confirmDelete(name);
    });
    trash.setOnTouchPressed(e -> {
      e.consume();
      confirmDelete(name);
    });
    trash.setCursor(Cursor.HAND);
    trash.setVisible(false);
    trash.setTooltip(new Tooltip("Delete " + name));
    trash.getStyleClass().add("trash-can");

    final Label text = new Label(name);
    text.setMaxWidth(110);

    badge = new JFXBadge(new Label());
    badge.setAlignment(Pos.CENTER_LEFT);
    updateCount(count);

    initListeners();

    setLeft(trash);
    setAlignment(trash, Pos.TOP_LEFT);
    setCenter(text);
    setAlignment(text, Pos.CENTER_LEFT);
    setMargin(text, new Insets(0, 0, 0, 10));
    setRight(badge);
    setAlignment(badge, Pos.CENTER_RIGHT);
  }

  private void confirmDelete(final String name) {
    final PopOver confirmationDialog = new PopOver();

    final BorderPane contents = new DeleteConfirmationPane(name,
        () -> {
          confirmationDialog.hide();
          onDelete.accept(name);
        },
        confirmationDialog::hide);
    confirmationDialog.setContentNode(contents);
    confirmationDialog.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
    confirmationDialog.show(trash);
  }

  private void initListeners() {
    editableProperty.addListener((observable, oldValue, newValue) -> trash.setVisible(newValue));
  }

  BooleanProperty editablePropertyProperty() {
    return editableProperty;
  }

  void setEditable(final boolean editable) {
    editableProperty.set(editable);
  }

  void setOnDelete(final Consumer<String> onDelete) {
    this.onDelete = Objects.requireNonNull(onDelete, "onDelete");
  }

  void updateCount(final int count) {
    if (count > 0) {
      badge.setText(count > 99 ? _99_PLUS : String.valueOf(count));
    } else {
      badge.setEnabled(false);
    }
  }

  private static class DeleteConfirmationPane extends BorderPane {

    DeleteConfirmationPane(final String name, final Runnable onYes, final Runnable onNo) {
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
      final Label categoryName = new Label(name);
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
