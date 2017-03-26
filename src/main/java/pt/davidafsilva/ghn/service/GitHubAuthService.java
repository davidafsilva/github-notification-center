package pt.davidafsilva.ghn.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import io.netty.handler.codec.http.HttpResponseStatus;
import pt.davidafsilva.ghn.ApplicationOptions;
import pt.davidafsilva.ghn.model.User;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientException;
import reactor.ipc.netty.http.client.HttpClientRequest;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;

/**
 * @author david
 */
public class GitHubAuthService {

  // initialize the request body
  private static final String AUTH_REQUEST_BODY = "{\n" +
      "  \"scopes\": [\n" +
      "    \"notifications\"\n" +
      "  ],\n" +
      "  \"note\": \"GitHub Notification Hub token\"\n" +
      "}";
  // url path
  static final String AUTH_URL_PATH = "/authorizations";

  // properties
  private final HttpClient client;
  private final String url;

  public GitHubAuthService(final ApplicationOptions applicationOptions) {
    this.client = HttpClient.create();
    this.url = applicationOptions.getGitHubScheme() + "://" + applicationOptions.getGitHubHost() +
        ":" + applicationOptions.getGitHubPort() + AUTH_URL_PATH;
  }

  public Mono<User> authenticate(final String user, final String password, final String code) {
    return client.post(url, request -> sendRequest(request, user, password, code))
        .mapError(this::is2FactorRequired, TwoFactorAuthRequiredException::new)
        .mapError(this::isUnauthorizedOrForbidden, InvalidCredentialsException::new)
        .filter(r ->
            r.status() == HttpResponseStatus.OK || r.status() == HttpResponseStatus.CREATED)
        .flatMap(response -> response
            .receive()
            .asInputStream()
            .map(in -> {
              final ObjectMapper mapper = new ObjectMapper();
              try {
                return mapper.readTree(in);
              } catch (final IOException e) {
                throw Exceptions.propagate(e);
              } finally {
                response.dispose();
              }
            }))
        .filter(json -> json.has("hashed_token"))
        .map(json -> new User(user, json.get("hashed_token").asText()))
        .next();
  }

  private Publisher<Void> sendRequest(final HttpClientRequest request,
      final String user, final String password, final String code) {
    return Mono.just(request)
        .map(this::addUserAgent)
        .map(r -> addOtpHeader(r, code))
        .map(r -> addAuthorization(r, user, password))
        .map(r -> r.sendString(Mono.just(AUTH_REQUEST_BODY)))
        .block();
  }

  private HttpClientRequest addOtpHeader(final HttpClientRequest request, final String code) {
    return Mono.justOrEmpty(code)
        .map(c -> request.header("X-GitHub-OTP", c))
        .defaultIfEmpty(request)
        .block();
  }

  private HttpClientRequest addUserAgent(final HttpClientRequest request) {
    return request.header(USER_AGENT, "ghn/1.0");
  }

  private HttpClientRequest addAuthorization(final HttpClientRequest request,
      final String user, final String password) {
    final ByteBuffer buf = StandardCharsets.ISO_8859_1.encode(user + ':' + password);
    final String encoded = Base64.getEncoder().encodeToString(buf.array());
    return request.header(AUTHORIZATION, "basic " + encoded);
  }

  private boolean isUnauthorizedOrForbidden(final Throwable e) {
    if (HttpClientException.class.isInstance(e)) {
      final HttpClientException clientException = (HttpClientException) e;
      return Objects.equals(clientException.status(), HttpResponseStatus.UNAUTHORIZED) ||
          Objects.equals(clientException.status(), HttpResponseStatus.FORBIDDEN);
    }
    return false;
  }

  private boolean is2FactorRequired(final Throwable throwable) {
    final HttpClientException httpClientException = (HttpClientException) throwable;
    final String otpHeader = httpClientException.headers().get("X-GitHub-OTP", "");
    return otpHeader.startsWith("required");
  }
}
