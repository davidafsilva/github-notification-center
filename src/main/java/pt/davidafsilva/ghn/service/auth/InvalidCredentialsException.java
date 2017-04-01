package pt.davidafsilva.ghn.service.auth;

import pt.davidafsilva.ghn.service.GhnException;

/**
 * @author david
 */
public final class InvalidCredentialsException extends GhnException {

  InvalidCredentialsException(final Throwable e) {
    super("Invalid credentials", e);
  }
}
