package pt.davidafsilva.ghn.ui.main;

import java.util.UUID;

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
    category.setPostFilter(filter);
    category.setEditable(true);
    category.setDeletable(true);
    appContext.getCategoryService().save(category)
        .doOnError(e -> notificationsView.onCategorySaveComplete(e.getMessage()))
        .doOnNext(notificationsView::addCategory)
        .doOnSuccess(c -> notificationsView.onCategorySaveComplete(null));
  }

  void deleteCategory(final Category category) {
    appContext.getCategoryService().delete(category)
        .doOnSuccess(v -> notificationsView.removeCategory(category));
  }

  public void loadNotifications() {
    //FIXME
  }
}
