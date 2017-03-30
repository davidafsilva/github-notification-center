package pt.davidafsilva.ghn.service;

/**
 * @author david
 */
public final class TokenExistsException extends GhnException {

  TokenExistsException(final Throwable e) {
    super("Token already created", e);
  }
}
