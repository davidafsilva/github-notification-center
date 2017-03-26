package pt.davidafsilva.ghn.service;

/**
 * @author david
 */
public final class TwoFactorAuthRequiredException extends GhnException {

  TwoFactorAuthRequiredException(final Throwable e) {
    super("2-Factor authentication required", e);
  }
}
