package pt.davidafsilva.ghn.service.storage;

import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author david
 */
public interface StorageService {

  <O extends AbstractModel & Persisted> Flux<O> readAll(final Class<O> type);

  <O extends AbstractModel & Persisted> Mono<O> read(final Class<O> type, final String key);

  <O extends AbstractModel & Persisted> Mono<Void> write(final O value);

  <O extends AbstractModel & Persisted> Mono<Void> delete(final O value);

}
