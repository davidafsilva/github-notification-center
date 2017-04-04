package pt.davidafsilva.ghn.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import pt.davidafsilva.ghn.ApplicationContext;

/**
 * @author david
 */
public final class AuthorizationFacility {

  // the token length
  private static final int GITHUB_TOKEN_LENGTH = 40;

  public static String createHeaderValueFor(final String token) {
    return "token " + token;
  }

  public static String createHeaderValueFor(final String username, final String password) {
    final ByteBuffer buf = StandardCharsets.ISO_8859_1.encode(username + ':' + password);
    final String encoded = Base64.getEncoder().encodeToString(buf.array());
    return "basic " + encoded;
  }

  public static String createHeaderValueFor(final ApplicationContext context) {
    return context.getOptions().getToken()
        .map(AuthorizationFacility::createHeaderValueFor)
        .orElseGet(() -> {
          final String credentials = context.getUser().getCredentials();
          return isToken(credentials) ?
              createHeaderValueFor(credentials) :
              createHeaderValueFor(context.getUser().getUsername(), credentials);
        });
  }

  public static boolean isToken(final String credential) {
    return credential.length() == GITHUB_TOKEN_LENGTH;
  }
}
