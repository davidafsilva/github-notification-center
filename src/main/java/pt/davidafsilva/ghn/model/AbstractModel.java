package pt.davidafsilva.ghn.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author david
 */
public class AbstractModel {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .configure(SerializationFeature.INDENT_OUTPUT, true);

  @Override
  public String toString() {
    try {
      return MAPPER.writerFor(getClass()).writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException("unable to serialize object", e);
    }
  }
}
