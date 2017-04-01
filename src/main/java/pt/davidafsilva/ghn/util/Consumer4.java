package pt.davidafsilva.ghn.util;

/**
 * @author david
 */
@FunctionalInterface
public interface Consumer4<T, U, V, X> {

  /**
   * Performs this operation to the given arguments.
   *
   * @param t the first function argument
   * @param u the second function argument
   * @param v the third function argument
   * @param x the fourth function argument
   */
  void accept(T t, U u, V v, X x);

}
