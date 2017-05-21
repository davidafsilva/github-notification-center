package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author david
 */
public final class PostFilterDeserializer extends JsonDeserializer<PostFilter> {

  @Override
  public PostFilter deserialize(final JsonParser p, final DeserializationContext context)
      throws IOException {
    try (final ByteArrayInputStream buffer = new ByteArrayInputStream(p.getBinaryValue());
        final ObjectInputStream iis = new ObjectInputStream(buffer)) {
      try {
        return (PostFilter) iis.readObject();
      } catch (final ClassNotFoundException e) {
        throw new IOException(e);
      }
    }
  }
}
