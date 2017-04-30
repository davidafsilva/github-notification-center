package pt.davidafsilva.ghn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Objects;

import io.netty.handler.codec.http.HttpResponseStatus;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.model.mutable.Configuration;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientException;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCEPT;
import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;
import static pt.davidafsilva.ghn.util.AuthorizationFacility.createHeaderValueFor;

/**
 * @author david
 */
public class AbstractGitHubService {

  private static final String GITHUB_OTP_HEADER = "X-GitHub-OTP";
  private static final String ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json";
  private static final String VERSION = "ghn/1.0";

  protected HttpClientRequest addOtpHeader(final HttpClientRequest request, final String code) {
    return Mono.justOrEmpty(code)
        .map(c -> request.header(GITHUB_OTP_HEADER, c))
        .defaultIfEmpty(request)
        .block();
  }

  protected HttpClientRequest addUserAgent(final HttpClientRequest request) {
    return request.header(USER_AGENT, VERSION);
  }

  protected HttpClientRequest addAcceptHeeader(final HttpClientRequest request) {
    return request.header(ACCEPT, ACCEPT_HEADER_VALUE);
  }

  protected HttpClientRequest addAuthorization(final HttpClientRequest request,
      final ApplicationContext context) {
    return request.header(AUTHORIZATION, createHeaderValueFor(context));
  }

  protected HttpClientRequest addBasicAuthorization(final HttpClientRequest request,
      final String user, final String password) {
    return request.header(AUTHORIZATION, createHeaderValueFor(user, password));
  }

  protected HttpClientRequest addTokenAuthorization(final HttpClientRequest request,
      final String token) {
    return request.header(AUTHORIZATION, createHeaderValueFor(token));
  }

  protected boolean isTokenAlreadyCreated(final Throwable throwable) {
    if (HttpClientException.class.isInstance(throwable)) {
      final HttpClientException clientException = (HttpClientException) throwable;
      return Objects.equals(clientException.status(), HttpResponseStatus.UNPROCESSABLE_ENTITY);
    }
    return false;
  }

  protected boolean isUnauthorizedOrForbidden(final Throwable throwable) {
    if (HttpClientException.class.isInstance(throwable)) {
      final HttpClientException clientException = (HttpClientException) throwable;
      return Objects.equals(clientException.status(), HttpResponseStatus.UNAUTHORIZED) ||
          Objects.equals(clientException.status(), HttpResponseStatus.FORBIDDEN);
    }
    return false;
  }

  protected boolean is2FactorRequired(final Throwable throwable) {
    if (HttpClientException.class.isInstance(throwable)) {
      final HttpClientException httpClientException = (HttpClientException) throwable;
      final String otpHeader = httpClientException.headers().get(GITHUB_OTP_HEADER, "");
      return otpHeader.startsWith("required");
    }
    return false;
  }

  protected String url(final Configuration options, final String path) {
    return options.getGithubUrl() + path;
  }

  protected Mono<JsonNode> decodeJson(final HttpClientResponse response, final ObjectMapper mapper) {
    return response
        .receive()
        .asInputStream()
        .reduce(SequenceInputStream::new)
        .map(in -> {
          try {
            return mapper.readTree(in);
          } catch (final IOException e) {
            throw Exceptions.propagate(e);
          } finally {
            response.dispose();
          }
        });
  }
}
