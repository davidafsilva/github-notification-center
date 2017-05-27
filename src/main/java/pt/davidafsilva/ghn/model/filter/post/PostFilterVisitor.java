package pt.davidafsilva.ghn.model.filter.post;

/**
 * @author david
 */
public interface PostFilterVisitor {

  void and(final PostFilter left, final PostFilter right);

  void or(final PostFilter left, final PostFilter right);

  void repoOwner(final StringFilter.Type matchType, final String matchValue);

  void repoName(final StringFilter.Type matchType, final String matchValue);

  void repoDescription(final StringFilter.Type matchType, final String matchValue);

  void notificationType(final StringFilter.Type matchType, final String matchValue);

  void notificationTitle(final StringFilter.Type matchType, final String matchValue);

}
