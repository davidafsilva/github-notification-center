package pt.davidafsilva.ghn.ui.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Consumer;

import javafx.application.Platform;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.ApplicationController;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.model.filter.post.PostFilter;
import pt.davidafsilva.ghn.model.mutable.Category;
import pt.davidafsilva.ghn.model.mutable.Configuration;
import pt.davidafsilva.ghn.service.category.CategoryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author david
 */
public class NotificationsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsController.class);

  private static final String GITHUB_REPO =
      "https://github.com/davidafsilva/github-notification-center";
  private static final String DAVIDAFSILVA_PT = "http://davidafsilva.pt";

  private final ApplicationController appController;
  private final ApplicationContext appContext;
  private final NotificationsView notificationsView;

  public NotificationsController(final ApplicationController appController) {
    this.appController = appController;
    this.appContext = appController.getApplicationContext();
    this.notificationsView = new NotificationsView(this);
  }

  public NotificationsView initView() {
    // create the view
    notificationsView.initView();
    return notificationsView;
  }

  Configuration getConfiguration() {
    return appContext.getConfiguration();
  }

  User getUser() {
    return appContext.getUser();
  }

  void openGitHub() {
    appContext.getHostServices().showDocument(GITHUB_REPO);
  }

  void openWebPage() {
    appContext.getHostServices().showDocument(DAVIDAFSILVA_PT);
  }

  public void loadCategories() {
    appContext.getCategoryService().load()
        .doOnNext(notificationsView::addCategory)
        .doOnTerminate(this::addDefaultCategories)
        .subscribe();
  }

  private void addDefaultCategories() {
    Platform.runLater(() -> {
      if (!notificationsView.hasCategories()) {
        final CategoryService categoryService = appContext.getCategoryService();
        final Mono<Category> all = categoryService.save(createDefaultCategory("All"));
        final Mono<Category> unread = categoryService.save(createDefaultCategory("Unread"));
        Flux.merge(all, unread)
            .doOnNext(notificationsView::addCategory)
            .subscribe();
      }
    });
  }

  private Category createDefaultCategory(final String name) {
    final Category category = new Category(UUID.randomUUID().toString());
    category.setName(name);
    category.setDeletable(false);
    category.setEditable(false);
    return category;
  }

  void createCategory(final String name, final PostFilter filter) {
    final Category category = new Category(UUID.randomUUID().toString());
    category.setName(name);
    category.setPostFilter(filter == PostFilter.NO_OP ? null : filter);
    category.setEditable(true);
    category.setDeletable(true);
    saveUpdateCategory(category, notificationsView::addCategory);
  }

  void updateCategory(final Category prev, final String name, final PostFilter filter) {
    final Category category = new Category(prev.getId());
    category.setName(name);
    category.setPostFilter(filter == PostFilter.NO_OP ? null : filter);
    category.setEditable(true);
    category.setDeletable(true);
    saveUpdateCategory(category, c -> notificationsView.updateCategory(prev.getName(), c));
  }

  private void saveUpdateCategory(final Category category, final Consumer<Category> saveConsumer) {
    appContext.getCategoryService().save(category)
        .doOnError(e -> notificationsView.onCategorySaveComplete(e.getMessage()))
        .doOnNext(saveConsumer)
        .doOnSuccess(c -> notificationsView.onCategorySaveComplete(null))
        .subscribe();
  }

  void deleteCategory(final Category category) {
    appContext.getCategoryService().delete(category)
        .doOnError(e -> LOGGER.error("unable to delete category", e))
        .doOnSuccess(v -> notificationsView.removeCategory(category))
        .subscribe();
  }

  public void loadNotifications() {
    //FIXME
  }
}
