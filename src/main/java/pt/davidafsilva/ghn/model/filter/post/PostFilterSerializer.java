package pt.davidafsilva.ghn.model.filter.post;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author david
 */
public final class PostFilterSerializer extends JsonSerializer<PostFilter> {

  @Override
  public void serialize(final PostFilter value, final JsonGenerator gen,
      final SerializerProvider serializers) throws IOException {
    try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
      oos.writeObject(value);
      gen.writeBinary(buffer.toByteArray());
    }
  }
}
