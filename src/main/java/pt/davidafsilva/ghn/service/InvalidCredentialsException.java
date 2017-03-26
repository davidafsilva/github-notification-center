package pt.davidafsilva.ghn.service;

/**
 * @author david
 */
public final class InvalidCredentialsException extends GhnException {

  InvalidCredentialsException(final Throwable e) {
    super("Invalid credentials", e);
  }
}
