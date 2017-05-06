package pt.davidafsilva.ghn.ui.main;

import java.util.function.Consumer;

import javafx.scene.layout.BorderPane;
import pt.davidafsilva.ghn.model.mutable.Category;

/**
 * @author david
 */
class CreateCategoryView extends BorderPane {

  CreateCategoryView(final Consumer<Category> onSave, final Runnable onClose) {
    getStyleClass().add("ghn-create-category-pane");
  }
}
