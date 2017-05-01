package pt.davidafsilva.ghn.service.configuration;

import java.util.concurrent.atomic.AtomicReference;

import pt.davidafsilva.ghn.model.mutable.Configuration;
import pt.davidafsilva.ghn.model.mutable.SecuredConfiguration;
import pt.davidafsilva.ghn.service.storage.SecureStorageService;
import pt.davidafsilva.ghn.service.storage.StorageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author david
 */
public class ApplicationConfigurationService {

  private final StorageService storageService;
  private final SecureStorageService secureStorageService;

  private AtomicReference<Configuration> configuration = new AtomicReference<>();

  public ApplicationConfigurationService(
      final StorageService storageService,
      final SecureStorageService secureStorageService) {
    this.storageService = storageService;
    this.secureStorageService = secureStorageService;
  }

  public Mono<Configuration> load() {
    if (configuration.get() == null) {
      return loadConfiguration()
          .doOnNext(configuration::set);
    }
    return Mono.just(configuration.get());
  }

  private Mono<Configuration> loadConfiguration() {
    // load unsecure bit
    return storageService.readAll(Configuration.class)
        .next()
        .subscribe()
        .otherwiseIfEmpty(Mono.defer(() -> Mono.just(new Configuration())))
        // then load the secure bit
        .then(cfg -> secureStorageService.read(SecuredConfiguration.class, SecuredConfiguration.ID)
            .doOnNext(cfg::setSecuredConfiguration)
            .map(s -> cfg))
        .subscribe();
  }

  public Mono<Void> save(final Configuration configuration) {
    this.configuration.set(configuration);

    // unsecure config
    final Mono<Void> unsecureSave = storageService.write(Configuration.class, configuration);

    // secure config
    final SecuredConfiguration secureConfig = configuration.getSecuredConfiguration();
    final Mono<Void> secureSave = secureConfig == null ? Mono.empty() :
        secureStorageService.write(SecuredConfiguration.class, secureConfig);

    // merge both saves
    return Flux.merge(unsecureSave, secureSave)
        .then()
        .subscribe();
  }
}
