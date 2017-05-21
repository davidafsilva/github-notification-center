package pt.davidafsilva.ghn.service.storage;

import com.couchbase.lite.Context;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.JavaContext;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.View;

import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import oshi.SystemInfo;
import pt.davidafsilva.ghn.model.AbstractModel;
import pt.davidafsilva.ghn.model.Persisted;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;
import static pt.davidafsilva.ghn.model.AbstractModel.marshall;
import static pt.davidafsilva.ghn.model.AbstractModel.unmarshall;

/**
 * @author david
 */
class CouchbaseFileBackedStorageService implements StorageService {

  // logger
  private static final Logger LOGGER = getLogger(CouchbaseFileBackedStorageService.class);
  private static final String VERSION = "1.0.0";

  private static final class DatabaseHolder {

    private static final Database database;

    static {
      try {
        // initialize the database
        final Context context = getContext();
        final Manager manager = new Manager(context, Manager.DEFAULT_OPTIONS);
        database = manager.getDatabase("ghnc");

        LOGGER.info("database initialized");
      } catch (final Exception e) {
        LOGGER.error("unable to initialize database", e);
        throw new ExceptionInInitializerError(e);
      }
    }

    private static Context getContext() {
      return new JavaContext("database") {
        @Override
        public File getRootDirectory() {
          final File rootDir;
          final String userHome = System.getProperty("user.home");
          switch (SystemInfo.getCurrentPlatformEnum()) {
            case WINDOWS:
              rootDir = new File(userHome, "GHNC");
              break;
            default:
              rootDir = new File(userHome, ".ghnc");
              break;
          }
          return rootDir;
        }
      };
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <O extends AbstractModel & Persisted> Flux<O> readAll(final Class<O> type) {
    // defer execution
    return Flux.defer(() -> {
      try {
        final View view = view(type);
        final QueryOptions options = new QueryOptions();
        return Flux.fromIterable(view.query(options))
            .map(row -> {
              if (type.isInstance(row.getValue())) {
                return (O) row.getValue();
              } else {
                return unmarshall(type, DatabaseHolder.database.getDocument(row.getDocumentId())
                    .getProperty("value").toString());
              }
            });
      } catch (CouchbaseLiteException e) {
        LOGGER.error("unable to read all for type: {}", type);
        return Flux.error(e);
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public <O extends AbstractModel & Persisted> Mono<O> read(final Class<O> type,
      final String key) {
    // get the database ref
    final Database database = DatabaseHolder.database;

    // create the query
    return Mono.justOrEmpty(database.getDocument(key))
        .map(doc -> unmarshall(type, doc.getProperty("value").toString()));
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<Void> write(final O value) {
    // get the database ref
    final Database database = DatabaseHolder.database;
    try {
      final Document document = database.getDocument(value.getId());
      Map<String, Object> properties = document.getProperties();
      if (properties == null) {
        properties = new HashMap<>();
        properties.put("type", value.getClass().getCanonicalName());
      }
      properties.put("value", marshall(value));
      document.putProperties(properties);
      return Mono.empty();
    } catch (final CouchbaseLiteException e) {
      LOGGER.error("unable to save id: {}", value.getId());
      return Mono.error(e);
    }
  }

  @Override
  public <O extends AbstractModel & Persisted> Mono<Void> delete(final O value) {
    // get the database ref
    final Database database = DatabaseHolder.database;
    try {
      final Document document = database.getDocument(value.getId());
      document.delete();
      return Mono.empty();
    } catch (final CouchbaseLiteException e) {
      LOGGER.error("unable to save id: {}", value.getId());
      return Mono.error(e);
    }
  }

  private static <O extends AbstractModel & Persisted> View view(
      final Class<O> clazz) throws CouchbaseLiteException {
    final View view = DatabaseHolder.database.getView(clazz.getSimpleName() + "TypeView");
    if (view.getMap() == null) {
      view.setMap((document, emitter) -> {
        final Object type = document.get("type");
        if (Objects.equals(type, clazz.getCanonicalName())) {
          final O value = unmarshall(clazz, document.get("value").toString());
          emitter.emit(value.getId(), value);
        }
      }, VERSION);
      view.updateIndex();
    }
    return view;
  }
}
