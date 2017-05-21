package pt.davidafsilva.ghn.service.storage;

import org.slf4j.Logger;

import java.util.Base64;

import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;
import static pt.davidafsilva.ghn.model.AbstractModel.marshall;
import static pt.davidafsilva.ghn.model.AbstractModel.unmarshall;

/**
 * @author david
 */
abstract class CryptoBackedStorageService implements SecureStorageService {

  static final Logger LOGGER = getLogger(CryptoBackedStorageService.class);

  private final StorageService decorated;

  CryptoBackedStorageService(final StorageService decorated) {
    this.decorated = decorated;
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<Void> write(final O value) {
    return Mono.defer(() -> Mono.just(marshall(value))
        // protect data + encode in b64
        .map(serialized -> wrap(value.getClass(), value.getId(), serialized))
        // save data
        .flatMap(secureEntry -> {
          try {
            return decorated.write(secureEntry);
          } catch (final Exception e) {
            LOGGER.error("unable to save property", e);
            return Mono.error(e);
          }
        }).next());
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<Void> delete(final O value) {
    return decorated.delete(value);
  }

  @Override
  public <O extends AbstractModel & Persisted> Flux<O> readAll(final Class<O> type) {
    try {
      // read data
      return decorated.readAll(SecuredValue.class)
          .filter(value -> value.getActualClass() == type)
          .map(value -> unwrap(type, value));
    } catch (final Exception e) {
      LOGGER.error("unable to read property", e);
      return Flux.error(e);
    }
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<O> read(final Class<O> type,
      final String key) {
    try {
      // read data
      return decorated.read(SecuredValue.class, key)
          .filter(value -> value.getActualClass() == type)
          .map(value -> unwrap(type, value));
    } catch (final Exception e) {
      LOGGER.error("unable to read property", e);
      return Mono.error(e);
    }
  }

  private <O extends AbstractModel> SecuredValue wrap(final Class<O> type,
      final String key, final String serialized) {
    final String secured = Base64.getEncoder().withoutPadding().encodeToString(
        cipherData(serialized.getBytes(), key.getBytes()));
    return new SecuredValue(type, key, secured);
  }

  private <O extends AbstractModel & Persisted> O unwrap(final Class<O> type,
      final SecuredValue value) {
    // decode from base64 form
    final byte[] data = Base64.getDecoder().decode(value.getValue());
    // unprotect
    final String unprotected = new String(decipherData(data, value.getId().getBytes()));
    // unmarshall the data
    return unmarshall(type, unprotected);
  }

  abstract byte[] cipherData(final byte[] value, final byte[] entropy);

  abstract byte[] decipherData(final byte[] value, final byte[] entropy);

  private static class SecuredValue extends AbstractModel implements Persisted {

    private final Class<?> actualClass;
    private final String key;
    private final String value;

    private SecuredValue(final Class<?> actualClass, final String key, final String value) {
      this.actualClass = actualClass;
      this.key = key;
      this.value = value;
    }

    Class<?> getActualClass() {
      return actualClass;
    }

    String getValue() {
      return value;
    }

    @Override
    public String getId() {
      return key;
    }
  }
}
