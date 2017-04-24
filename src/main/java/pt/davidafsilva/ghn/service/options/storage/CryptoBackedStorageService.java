package pt.davidafsilva.ghn.service.options.storage;

import org.slf4j.Logger;

import java.util.Base64;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

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
  public boolean write(final String key, final String value) {
    try {
      // protect data + encode in b64
      final String data = Base64.getEncoder().withoutPadding().encodeToString(
          cipherData(value.getBytes(), key.getBytes()));

      // save data
      return decorated.write(key, data);
    } catch (final Exception e) {
      LOGGER.error("unable to save property", e);
    }

    return false;
  }

  @Override
  public Optional<String> read(final String key) {
    try {
      // read data
      return decorated.read(key)
          // decode from base64 form
          .map(Base64.getDecoder()::decode)
          // unprotect
          .map(data -> decipherData(data, key.getBytes()))
          // format as string back again
          .map(String::new);
    } catch (final Exception e) {
      LOGGER.error("unable to read property", e);
    }
    return Optional.empty();
  }

  abstract byte[] cipherData(final byte[] value, final byte[] entropy);

  abstract byte[] decipherData(final byte[] value, final byte[] entropy);
}
