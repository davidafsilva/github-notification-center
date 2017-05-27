package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * @author david
 */
public class StringFilter {

  @JsonProperty
  private final Type type;
  @JsonProperty
  private final String comparisonValue;

  @JsonCreator
  private StringFilter(
      @JsonProperty(value = "type", required = true) final Type type,
      @JsonProperty(value = "comparisonValue", required = true) final String comparisonValue) {
    this.type = type;
    this.comparisonValue = comparisonValue;
  }

  boolean filter(final String value) {
    return type.tester.test(value, comparisonValue);
  }

  public String getComparisonValue() {
    return comparisonValue;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    CONTAINS(String::contains),
    STARTS_WITH(String::startsWith),
    ENDS_WITH(String::endsWith),
    REGEX(String::matches);

    private final BiPredicate<String, String> tester;

    Type(final BiPredicate<String, String> tester) {this.tester = tester;}
  }

  public static StringFilter contains(final String value) {
    return new StringFilter(Type.CONTAINS, value);
  }

  public static StringFilter startsWith(final String value) {
    return new StringFilter(Type.STARTS_WITH, value);
  }

  public static StringFilter endsWith(final String value) {
    return new StringFilter(Type.ENDS_WITH, value);
  }

  public static StringFilter regex(final String regex) {
    Objects.nonNull(Pattern.compile(regex));
    return new StringFilter(Type.REGEX, regex);
  }
}
