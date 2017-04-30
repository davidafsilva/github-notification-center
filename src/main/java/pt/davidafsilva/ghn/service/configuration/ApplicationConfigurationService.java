package pt.davidafsilva.ghn.service.configuration;

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

  private Configuration configuration;

  public ApplicationConfigurationService(
      final StorageService storageService,
      final SecureStorageService secureStorageService) {
    this.storageService = storageService;
    this.secureStorageService = secureStorageService;
  }

  public Configuration load() {
    if (configuration == null) {
      configuration = loadConfiguration();
    }
    return configuration;
  }

  private Configuration loadConfiguration() {
    // load unsecure bit
    final Configuration configuration = storageService.readAll(Configuration.class)
        .next()
        .subscribe()
        .otherwiseIfEmpty(Mono.defer(() -> Mono.just(new Configuration())))
        .block();

    // load secure bit
    secureStorageService.read(SecuredConfiguration.class, SecuredConfiguration.ID)
        .doOnNext(configuration::setSecuredConfiguration)
        .subscribe()
        .block();

    return configuration;
  }

  public Mono<Void> save(final Configuration configuration) {
    this.configuration = configuration;
    final Mono<Void> unsecureSave = storageService.write(Configuration.class, configuration);
    final Mono<Void> secureSave = configuration.getSecuredConfiguration() == null ?
        Mono.empty() :
        secureStorageService.write(SecuredConfiguration.class, configuration.getSecuredConfiguration());
    return Flux.merge(unsecureSave, secureSave)
        .then()
        .subscribe();
  }
}
