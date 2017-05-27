package pt.davidafsilva.ghn.model.filter.post;

/**
 * @author david
 */
public enum PostFilterType {
  NO_OP,
  AND,
  OR,
  REPOSITORY_OWNER,
  REPOSITORY_NAME,
  REPOSITORY_DESCRIPTION,
  NOTIFICATION_TYPE,
  NOTIFICATION_TITLE,
}
