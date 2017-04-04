package pt.davidafsilva.ghn.service.auth;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.netty.handler.codec.http.HttpResponseStatus;
import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.model.User;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientException;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;
import static pt.davidafsilva.ghn.util.AuthorizationFacility.createHeaderValueFor;

/**
 * @author david
 */
public class GitHubAuthService {

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
  private static final String GITHUB_OTP_HEADER = "X-GitHub-OTP";

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
        .flatMap(this::decodeJson)
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
        .flatMap(this::decodeJson)
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

  private Mono<JsonNode> decodeJson(final HttpClientResponse response) {
    return response
        .receive()
        .asInputStream()
        .reduce(SequenceInputStream::new)
        .map(in -> {
          final ObjectMapper mapper = new ObjectMapper();
          try {
            return mapper.readTree(in);
          } catch (final IOException e) {
            throw Exceptions.propagate(e);
          } finally {
            response.dispose();
          }
        });
  }

  private Publisher<Void> sendRequest(final HttpClientRequest request,
      final String user, final String password, final String code,
      final Function<HttpClientRequest, BiFunction<String, String, HttpClientRequest>> authStrategy,
      final Function<HttpClientRequest, Publisher<Void>> sendFunction) {
    return Mono.just(request)
        .map(this::addUserAgent)
        .map(r -> addOtpHeader(r, code))
        .map(r -> authStrategy.apply(r).apply(user, password))
        .map(sendFunction)
        .block();
  }

  private HttpClientRequest addOtpHeader(final HttpClientRequest request, final String code) {
    return Mono.justOrEmpty(code)
        .map(c -> request.header(GITHUB_OTP_HEADER, c))
        .defaultIfEmpty(request)
        .block();
  }

  private HttpClientRequest addUserAgent(final HttpClientRequest request) {
    return request.header(USER_AGENT, "ghn/1.0");
  }

  private HttpClientRequest addBasicAuthorization(final HttpClientRequest request,
      final String user, final String password) {
    return request.header(AUTHORIZATION, createHeaderValueFor(user, password));
  }

  private HttpClientRequest addTokenAuthorization(final HttpClientRequest request,
      final String token) {
    return request.header(AUTHORIZATION, createHeaderValueFor(token));
  }

  private boolean isTokenAlreadyCreated(final Throwable throwable) {
    if (HttpClientException.class.isInstance(throwable)) {
      final HttpClientException clientException = (HttpClientException) throwable;
      return Objects.equals(clientException.status(), HttpResponseStatus.UNPROCESSABLE_ENTITY);
    }
    return false;
  }

  private boolean isUnauthorizedOrForbidden(final Throwable throwable) {
    if (HttpClientException.class.isInstance(throwable)) {
      final HttpClientException clientException = (HttpClientException) throwable;
      return Objects.equals(clientException.status(), HttpResponseStatus.UNAUTHORIZED) ||
          Objects.equals(clientException.status(), HttpResponseStatus.FORBIDDEN);
    }
    return false;
  }

  private boolean is2FactorRequired(final Throwable throwable) {
    if (HttpClientException.class.isInstance(throwable)) {
      final HttpClientException httpClientException = (HttpClientException) throwable;
      final String otpHeader = httpClientException.headers().get(GITHUB_OTP_HEADER, "");
      return otpHeader.startsWith("required");
    }
    return false;
  }

  private String url(final ApplicationOptions options, final String path) {
    return options.getGitHubScheme() + "://" + options.getGitHubHost() + ":" +
        options.getGitHubPort() + path;
  }
}
