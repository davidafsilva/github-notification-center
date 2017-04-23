package pt.davidafsilva.ghn.ui.main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.util.Comparator;
import java.util.Objects;
import java.util.TreeMap;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import pt.davidafsilva.ghn.model.mutable.Category;

/**
 * @author david
 */
class LeftSideView extends BorderPane {

  private static final int PHOTO_CIRCLE_RADIUS = 48;

  private final NotificationsController controller;
  private final ObservableMap<String, CategoryItem> categoriesMap = FXCollections
      .observableMap(new TreeMap<String, CategoryItem>(Comparator.naturalOrder()));

  private JFXListView<CategoryItem> categories;
  private JFXToggleButton editButton;

  LeftSideView(final NotificationsController controller) {
    this.controller = controller;
    getStyleClass().add("ghn-left-panel");
    initialize();
  }

  private void initialize() {
    final VBox panel = new VBox();
    panel.setPadding(new Insets(15));
    panel.setSpacing(20);

    // photo and user panel
    createPhotoUserPanel(panel);
    createDivider(panel);

    // categories
    final Node categoriesPane = createCategories();

    // app information
    final Node appInfoPane = createAppInfoPanel();

    // set the contents
    setTop(panel);
    setCenter(categoriesPane);
    setBottom(appInfoPane);
  }

  private void createPhotoUserPanel(final VBox panel) {
    // user & photo panel
    final VBox photoUserPanel = new VBox();

    // photo
    photoUserPanel.setFillWidth(true);
    photoUserPanel.setSpacing(10);
    final ImageView photoImage = new ImageView(loadImage(getDefaultAvatar(), false));
    photoImage.setClip(new Circle(PHOTO_CIRCLE_RADIUS, PHOTO_CIRCLE_RADIUS, PHOTO_CIRCLE_RADIUS));
    final Label photo = new Label("", photoImage);
    photo.setAlignment(Pos.CENTER);
    photo.setMaxWidth(Double.MAX_VALUE);
    photoUserPanel.getChildren().add(photo);
    // lazily load the actual image from the network
    controller.getUser().getAvatarUrl().ifPresent(url -> {
      final Image img = loadImage(url, true);
      img.progressProperty().addListener((ob, old, val) -> {
        if (val.doubleValue() == 1d) {
          photoImage.setImage(img);
        }
      });
    });

    // username
    final Label username = new Label(controller.getUser().getUsername());
    username.setAlignment(Pos.CENTER);
    username.setMaxWidth(Double.MAX_VALUE);
    username.getStyleClass().add("ghn-left-panel-username");
    photoUserPanel.getChildren().add(username);

    // add photo & username panel
    panel.getChildren().add(photoUserPanel);
  }

  private VBox createAppInfoPanel() {
    // container
    final VBox appInfoPanel = new VBox();
    appInfoPanel.getStyleClass().add("ghn-app-info");

    // divider
    createDivider(appInfoPanel);

    // contents
    final VBox contents = new VBox();
    contents.setAlignment(Pos.CENTER);
    final Label l1 = new Label("GitHub Notification Center");
    final HBox madeByLabel = new HBox();
    madeByLabel.setAlignment(Pos.CENTER);
    madeByLabel.setPadding(new Insets(5));
    madeByLabel.getChildren().add(new Label("Made with "));
    final Label l2 = new Label("\u2665");
    l2.getStyleClass().add("ghn-heart");
    madeByLabel.getChildren().add(l2);
    final Label personalLink = new Label(" by David Silva");
    personalLink.setOnMouseClicked(e -> controller.openWebPage());
    personalLink.setOnTouchReleased(e -> controller.openWebPage());
    madeByLabel.getChildren().add(personalLink);

    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    final Hyperlink githubIcon = new Hyperlink("", fontAwesome.create(FontAwesome.Glyph.GITHUB)
        .size(14));
    githubIcon.setBorder(Border.EMPTY);
    githubIcon.setOnAction(e -> controller.openGitHub());
    contents.getChildren().addAll(l1, madeByLabel, githubIcon);
    appInfoPanel.getChildren().add(contents);

    return appInfoPanel;
  }

  private void createDivider(final VBox panel) {
    final HBox divider = new HBox();
    divider.getStyleClass().add("ghn-divider");
    divider.prefWidthProperty().bind(panel.widthProperty().multiply(0.7));
    panel.getChildren().add(divider);
  }

  private Image loadImage(final String path, final boolean inBackground) {
    return new Image(path, PHOTO_CIRCLE_RADIUS * 2,
        PHOTO_CIRCLE_RADIUS * 2, true, true, inBackground);
  }

  private String getDefaultAvatar() {
    return getClass().getResource("/ghnc-96.png").toExternalForm();
  }

  private Node createCategories() {
    final BorderPane container = new BorderPane();
    container.getStyleClass().add("ghn-categories-pane");

    // top label and edit button
    final BorderPane topPane = new BorderPane();
    // categories label
    final Label label = new Label("CATEGORIES");
    label.getStyleClass().add("ghn-categories-label");
    topPane.setLeft(label);
    // edit button
    editButton = new JFXToggleButton();
    editButton.setText("EDIT");
    topPane.setRight(editButton);
    topPane.setVisible(false);
    container.setTop(topPane);

    // categories
    categories = new JFXListView<>();
    categories.getStyleClass().add("ghn-categories-list");
    categoriesMap.addListener((MapChangeListener<String, CategoryItem>) change -> {
      if (change.getValueRemoved() != null) {
        categories.getItems().remove(change.getValueRemoved());
      }
      if (change.getValueAdded() != null) {
        categories.getItems().add(change.getValueAdded());
      }
    });
    // loading
    final VBox loadingCategories = new VBox();
    loadingCategories.getStyleClass().add("ghn-categories-load");
    loadingCategories.setAlignment(Pos.CENTER);
    loadingCategories.setSpacing(20);
    loadingCategories.getChildren().add(new JFXSpinner());
    loadingCategories.getChildren().add(new Label("Loading categories..."));
    container.setCenter(loadingCategories);

    // create category
    final HBox createCategory = new HBox();
    createCategory.getStyleClass().add("ghn-create-category");
    final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    final Node plusSign = fontAwesome.create(FontAwesome.Glyph.PLUS_CIRCLE).size(15);
    final Label createLabel = new Label("Create Category");
    createCategory.getChildren().addAll(plusSign, createLabel);
    createCategory.visibleProperty().bind(editButton.selectedProperty());
    createCategory.setOnMousePressed(e -> showCreateCategoryView());
    createCategory.setOnTouchPressed(e -> showCreateCategoryView());
    container.setBottom(createCategory);

    return container;
  }

  private void showCreateCategoryView() {
    // FIXME: implement
  }

  void updateCategory(final String name, final Category category) {
    if (Objects.equals(name, category.getName())) {
      // name not updated
      Platform.runLater(() -> categoriesMap.computeIfPresent(name, (k, e) -> {
        e.editablePropertyProperty().unbind();
        final CategoryItem item = new CategoryItem(category);
        item.editablePropertyProperty().bind(editButton.selectedProperty());
        return item;
      }));
    } else {
      // remove previous
      removeCategory(name);
      addCategory(category);
    }
  }

  void addCategory(final Category category) {
    if (categoriesMap.isEmpty()) {
      // remove loading stuff
      final BorderPane center = (BorderPane) getCenter();
      Platform.runLater(() -> {
        center.getTop().setVisible(true);
        center.setCenter(categories);
      });
    }
    final CategoryItem item = new CategoryItem(category);
    item.editablePropertyProperty().bind(editButton.selectedProperty());
    Platform.runLater(() -> categoriesMap.put(category.getName(), item));
  }

  void removeCategory(final Category category) {
    removeCategory(category.getName());
  }

  private void removeCategory(final String name) {
    Platform.runLater(() -> categoriesMap.remove(name));
  }

}
