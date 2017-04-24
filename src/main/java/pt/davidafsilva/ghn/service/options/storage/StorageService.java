package pt.davidafsilva.ghn.service.options.storage;

import java.util.Optional;

/**
 * @author david
 */
public interface StorageService {

  boolean write(final String key, final String value);

  Optional<String> read(final String key);
}
