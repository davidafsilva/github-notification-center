package pt.davidafsilva.ghn.service.auth;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.netty.handler.codec.http.HttpResponseStatus;
import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.model.User;
import pt.davidafsilva.ghn.service.AbstractGitHubService;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;

/**
 * @author david
 */
public class GitHubAuthService extends AbstractGitHubService {

  // initialize the request body
  private static final String TOKEN_AUTH_REQUEST_BODY = "{\n" +
      "  \"scopes\": [\n" +
      "    \"notifications\"\n" +
      "  ],\n" +
      "  \"note\": \"GitHub Notification Hub token\"\n" +
      "}";
  // url path
  private static final String LOGIN_URL_PATH = "/user";
  private static final String TOKEN_URL_PATH = "/authorizations";
  private static final String GRAVATAR_URL = "https://www.gravatar.com/";

  // properties
  private final HttpClient client;
  private final String loginUrl;
  private final String tokenUrl;

  public GitHubAuthService(final ApplicationOptions applicationOptions) {
    this.client = HttpClient.create(opt -> opt.sslSupport().disablePool());
    this.loginUrl = url(applicationOptions, LOGIN_URL_PATH);
    this.tokenUrl = url(applicationOptions, TOKEN_URL_PATH);
  }

  public Mono<User> loginWithToken(final String token) {
    return doLogin(null, token, null, r -> (u, t) -> addTokenAuthorization(r, t));
  }

  public Mono<User> loginWithPassword(final String user, final String password, final String code) {
    return doLogin(user, password, code, r -> (u, p) -> addBasicAuthorization(r, u, p));
  }

  public Mono<String> createToken(final String user, final String password, final String code) {
    return client.post(tokenUrl, request -> sendRequest(request, user, password, code,
        r -> (u, p) -> addBasicAuthorization(r, u, p),
        r -> r.sendString(Mono.just(TOKEN_AUTH_REQUEST_BODY))))
        .mapError(this::is2FactorRequired, TwoFactorAuthRequiredException::new)
        .mapError(this::isUnauthorizedOrForbidden, InvalidCredentialsException::new)
        .mapError(this::isTokenAlreadyCreated, TokenExistsException::new)
        .filter(r -> r.status() == HttpResponseStatus.CREATED)
        .flatMap(r -> decodeJson(r, new ObjectMapper()))
        .filter(json -> json.has("token"))
        .map(json -> json.get("token").asText())
        .next();
  }

  private Mono<User> doLogin(final String u, final String p, final String c,
      final Function<HttpClientRequest, BiFunction<String, String, HttpClientRequest>> authStrategy) {
    return client.get(loginUrl, request -> sendRequest(request, u, p, c, authStrategy,
        HttpClientRequest::send))
        .mapError(this::is2FactorRequired, TwoFactorAuthRequiredException::new)
        .mapError(this::isUnauthorizedOrForbidden, InvalidCredentialsException::new)
        .filter(r -> r.status() == HttpResponseStatus.OK)
        .flatMap(r -> decodeJson(r, new ObjectMapper()))
        .map(json -> new User(getUsername(json), p, getAvatarUrl(json).orElse(null)))
        .next();
  }

  private String getUsername(final JsonNode json) {
    return json.get("login").asText();
  }

  private Optional<String> getAvatarUrl(final JsonNode json) {
    if (json.has("avatar_url")) {
      return Optional.of(json.get("avatar_url").asText());
    }
    if (json.has("gravatar_id")) {
      return Optional.of(GRAVATAR_URL + json.get("gravatar_id").asText());
    }

    return Optional.empty();
  }

  private Publisher<Void> sendRequest(final HttpClientRequest request,
      final String user, final String password, final String code,
      final Function<HttpClientRequest, BiFunction<String, String, HttpClientRequest>> authStrategy,
      final Function<HttpClientRequest, Publisher<Void>> sendFunction) {
    return Mono.just(request)
        .map(this::addUserAgent)
        .map(this::addAcceptHeeader)
        .map(r -> addOtpHeader(r, code))
        .map(r -> authStrategy.apply(r).apply(user, password))
        .map(sendFunction)
        .block();
  }
}
