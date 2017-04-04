package pt.davidafsilva.ghn.service.notification;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.reactivestreams.Publisher;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Callable;

import io.netty.handler.codec.http.HttpResponseStatus;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.util.AuthorizationFacility;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientException;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCEPT;
import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;

/**
 * @author david
 */
public class GitHubNotificationService {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private static final String NOTIFICATIONS_PATH = "/notifications";
  private static final String ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json";

  private final String url;
  private final ApplicationContext context;
  private final HttpClient client;

  public GitHubNotificationService(final ApplicationContext context) {
    this.context = context;
    this.client = HttpClient.create(opt -> opt.sslSupport().disablePool());
    this.url = context.getOptions().getGitHubScheme() + "://" +
        context.getOptions().getGitHubHost() + ":" +
        context.getOptions().getGitHubPort() + NOTIFICATIONS_PATH;
  }

  public Flux<Notification> getAllNotifications() {
    return getAllNotifications(null, null);
  }

  public Flux<Notification> getAllNotifications(final LocalDateTime from, final LocalDateTime to) {
    return client.get(url, this::requestHandler)
        .mapError(this::isUnauthorizedOrForbidden, UnauthorizedRequestException::new)
        .flatMap(this::mapNotificationFromResponse);
  }

  // -----------------
  // Utility methods
  // -----------------

  private boolean isUnauthorizedOrForbidden(final Throwable e) {
    if (HttpClientException.class.isInstance(e)) {
      final HttpClientException clientException = (HttpClientException) e;
      return Objects.equals(clientException.status(), HttpResponseStatus.UNAUTHORIZED) ||
          Objects.equals(clientException.status(), HttpResponseStatus.FORBIDDEN);
    }
    return false;
  }

  private Publisher<Void> requestHandler(final HttpClientRequest request) {
    return request
        .addHeader(ACCEPT, ACCEPT_HEADER_VALUE)
        .addHeader(AUTHORIZATION, AuthorizationFacility.createHeaderValueFor(context))
        .send();
  }

  private Flux<Notification> mapNotificationFromResponse(final HttpClientResponse response) {
    return response.receive().asInputStream()
        .map(in -> wrap(() -> objectMapper.readerFor(Notification.class)
            .<Notification>readValues(in)))
        .flatMap(iterator -> subscriber -> {
          while (iterator.hasNext()) {
            subscriber.onNext(iterator.next());
          }
          subscriber.onComplete();
        });
  }

  private static <T> T wrap(final Callable<T> callable) {
    try {
      return callable.call();
    } catch (final Exception e) {
      throw Exceptions.propagate(e);
    }
  }
}
