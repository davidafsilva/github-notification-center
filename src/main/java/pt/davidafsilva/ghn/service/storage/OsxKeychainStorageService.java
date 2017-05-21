package pt.davidafsilva.ghn.service.storage;

import org.slf4j.Logger;

import pt.davidafsilva.apple.OSXKeychain;
import pt.davidafsilva.apple.OSXKeychainException;
import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;
import static pt.davidafsilva.ghn.model.AbstractModel.marshall;
import static pt.davidafsilva.ghn.model.AbstractModel.unmarshall;

/**
 * @author david
 */
class OsxKeychainStorageService implements SecureStorageService {

  private static final Logger LOGGER = getLogger(OsxKeychainStorageService.class);
  private static final String username = System.getProperty("user.name");

  private final OSXKeychain keychain;

  OsxKeychainStorageService() {
    try {
      this.keychain = OSXKeychain.getInstance();
    } catch (final OSXKeychainException e) {
      throw new IllegalStateException("unable to load OS X keychain");
    }
  }

  @Override
  public <O extends AbstractModel & Persisted> Flux<O> readAll(final Class<O> type) {
    throw new UnsupportedOperationException("readAll() is not supported");
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<O> read(final Class<O> type,
      final String key) {
    return Mono.defer(() -> {
      try {
        // lookup in keychain
        return Mono.justOrEmpty(keychain.findGenericPassword(key, username)
            // deserialize data
            .map(serialized -> unmarshall(type, serialized))
            .orElse(null));
      } catch (OSXKeychainException e) {
        throw Exceptions.propagate(e);
      }
    });
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<Void> write(final O value) {
    return Mono.defer(() -> {
      try {
        // serialize
        final String serialized = marshall(value);

        // modify/create record in keychain
        keychain.modifyGenericPassword(value.getId(), username, serialized);
        return Mono.empty();
      } catch (final Exception e) {
        LOGGER.error("unable to save property", e);
        return Mono.error(e);
      }
    });
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<Void> delete(final O value) {
    return Mono.defer(() -> {
      try {
        // remove the record in keychain
        keychain.deleteGenericPassword(value.getId(), username);
        return Mono.empty();
      } catch (final Exception e) {
        LOGGER.error("unable to save property", e);
        return Mono.error(e);
      }
    });
  }
}
