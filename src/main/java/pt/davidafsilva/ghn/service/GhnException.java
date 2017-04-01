package pt.davidafsilva.ghn.service;

/**
 * @author david
 */
public class GhnException extends Exception {

  public GhnException(final String message, final Throwable e) {
    super(message, e);
  }
}
