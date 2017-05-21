package pt.davidafsilva.ghn.model.filter.post;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author david
 */
public class StringFilter implements Serializable {

  private final Predicate<String> tester;

  private <P extends Predicate<String> & Serializable> StringFilter(final P tester) {
    this.tester = tester;
  }

  boolean filter(final String value) {
    return tester.test(value);
  }

  public static StringFilter contains(final String value) {
    return new StringFilter((Predicate<String> & Serializable)(v) -> v.contains(value));
  }

  public static StringFilter startsWith(final String value) {
    return new StringFilter((Predicate<String> & Serializable)(v) -> v.startsWith(value));
  }

  public static StringFilter endsWith(final String value) {
    return new StringFilter((Predicate<String> & Serializable)(v) -> v.endsWith(value));
  }

  public static StringFilter regex(final String regex) {
    final Pattern p = Pattern.compile(regex);
    return new StringFilter((Predicate<String> & Serializable)(v) -> p.matcher(v).matches());
  }
}
