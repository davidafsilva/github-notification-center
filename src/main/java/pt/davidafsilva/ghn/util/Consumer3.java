package pt.davidafsilva.ghn.util;

/**
 * @author david
 */
@FunctionalInterface
public interface Consumer3<T, U, V> {

  /**
   * Performs this operation to the given arguments.
   *
   * @param t the first function argument
   * @param u the second function argument
   * @param v the third function argument
   */
  void accept(T t, U u, V v);

}
