package pt.davidafsilva.ghn.service.auth;

import pt.davidafsilva.ghn.service.GhnException;

/**
 * @author david
 */
public final class TokenExistsException extends GhnException {

  TokenExistsException(final Throwable e) {
    super("Token already created", e);
  }
}
