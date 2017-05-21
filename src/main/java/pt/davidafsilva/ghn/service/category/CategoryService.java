package pt.davidafsilva.ghn.service.category;

import pt.davidafsilva.ghn.model.mutable.Category;
import pt.davidafsilva.ghn.service.storage.StorageService;
import pt.davidafsilva.ghn.util.Schedulers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author david
 */
public class CategoryService {

  private final StorageService storageService;

  public CategoryService(final StorageService storageService) {
    this.storageService = storageService;
  }

  public Flux<Category> load() {
    return storageService.readAll(Category.class)
        .subscribeOn(Schedulers.io());
  }

  public Mono<Category> save(final Category category) {
    return storageService.write(category)
        .subscribeOn(Schedulers.io())
        .then(Mono.just(category))
        .subscribe();
  }

  public Mono<Void> delete(final Category category) {
    return storageService.delete(category)
        .subscribeOn(Schedulers.io())
        .subscribe();
  }
}
