package pt.davidafsilva.ghn.service.notification;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import io.netty.handler.codec.http.HttpResponseStatus;
import pt.davidafsilva.ghn.ApplicationContext;
import pt.davidafsilva.ghn.model.Notification;
import pt.davidafsilva.ghn.service.AbstractGitHubService;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

/**
 * @author david
 */
public class GitHubNotificationService extends AbstractGitHubService {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

  private static final String NOTIFICATIONS_PATH = "/notifications";

  private final String baseUrl;
  private final ApplicationContext context;
  private final HttpClient client;

  public GitHubNotificationService(final ApplicationContext context) {
    this.context = context;
    this.client = HttpClient.create(opt -> opt.sslSupport().disablePool());
    this.baseUrl = context.getOptions().getGitHubScheme() + "://" +
        context.getOptions().getGitHubHost() + ":" +
        context.getOptions().getGitHubPort() + NOTIFICATIONS_PATH;
  }

  public Flux<Notification> getNotifications(final NotificationFilter filter) {
    return client.get(filter.addParametersTo(baseUrl), this::sendRequest)
        .mapError(this::isUnauthorizedOrForbidden, UnauthorizedRequestException::new)
        .filter(r -> r.status() == HttpResponseStatus.OK)
        .flatMap(this::unmarshall);
  }

  // -----------------
  // Utility methods
  // -----------------

  private Publisher<Void> sendRequest(final HttpClientRequest request) {
    return Mono.just(request)
        .map(this::addUserAgent)
        .map(this::addAcceptHeeader)
        .map(r -> addAuthorization(r, context))
        .map(HttpClientRequest::send)
        .block();
  }

  private Flux<Notification> unmarshall(final HttpClientResponse response) {
    return response
        .receive()
        .asInputStream()
        .reduce(SequenceInputStream::new)
        .map(in -> {
          try {
            final ObjectReader reader = MAPPER.readerFor(Notification.Builder.class);
            return reader.<Notification.Builder>readValues(in);
          } catch (final IOException e) {
            throw Exceptions.propagate(e);
          } finally {
            response.dispose();
          }
        })
        .flatMap(this::toFlux);
  }

  private Flux<Notification> toFlux(final MappingIterator<Notification.Builder> iterator) {
    final Spliterator<Notification.Builder> spliterator = Spliterators.spliteratorUnknownSize(
        iterator, Spliterator.ORDERED);
    return Flux.fromStream(StreamSupport.stream(spliterator, false)
        .map(Notification.Builder::build));
  }
}
