package pt.davidafsilva.ghn.model.filter.post;

/**
 * @author david
 */
class AndPostFilter extends GroupPostFilter {

  AndPostFilter(final PostFilter left, final PostFilter right) {
    super(left, right);
  }
}
