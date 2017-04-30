package pt.davidafsilva.ghn.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * @author david
 */
public class AbstractModel {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());

  @Override
  public String toString() {
    return marshall(this);
  }

  public static <T extends AbstractModel> String marshall(final T obj) {
    try {
      return MAPPER.writerFor(obj.getClass()).writeValueAsString(obj);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException("unable to marshall object", e);
    }
  }

  public static <T extends AbstractModel> T unmarshall(final Class<T> clazz,
      final String serialized) {
    try {
      return MAPPER.readerFor(clazz).readValue(serialized);
    } catch (final IOException e) {
      throw new RuntimeException("unable to unmarshall object", e);
    }
  }
}
