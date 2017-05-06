package pt.davidafsilva.ghn.model.filter.pre;

/**
 * @author david
 */
public interface PreFilter {

  String appendTo(final String url);

   static PreFilterBuilder newBuilder() {
    return new PreFilterBuilder();
  }
}
