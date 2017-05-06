package pt.davidafsilva.ghn.model.filter.post;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author david
 */
public class StringFilter {

  private final Predicate<String> tester;

  private StringFilter(final Predicate<String> tester) {
    this.tester = tester;
  }

  boolean filter(final String value) {
    return tester.test(value);
  }

  public static StringFilter contains(final String value) {
    return new StringFilter(v -> v.contains(value));
  }

  public static StringFilter startsWith(final String value) {
    return new StringFilter(v -> v.startsWith(value));
  }

  public static StringFilter endsWith(final String value) {
    return new StringFilter(v -> v.endsWith(value));
  }

  public static StringFilter regex(final String regex) {
    final Pattern p = Pattern.compile(regex);
    return new StringFilter(v -> p.matcher(v).matches());
  }
}
