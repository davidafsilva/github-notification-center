package pt.davidafsilva.ghn.service.notification;

/**
 * @author david
 */
public final class UnauthorizedRequestException extends Exception {

  UnauthorizedRequestException(final Throwable e) {
    super("Session expired", e);
  }
}
