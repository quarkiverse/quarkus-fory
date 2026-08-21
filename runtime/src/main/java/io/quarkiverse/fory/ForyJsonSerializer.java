package io.quarkiverse.fory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.apache.fory.json.ForyJson;
import org.apache.fory.reflect.TypeRef;

/**
 * JAX-RS provider for {@code application/json} using Fory JSON.
 * <p>
 * Provides the same high-performance serialization as the binary Fory serializer
 * but produces standard JSON output consumable by browsers, CLI tools, and any
 * JSON-compliant client.
 * <p>
 * Deliberately not annotated with {@code @Provider}: it must only be registered when
 * {@code quarkus.fory.json.enabled=true}, so registration is done explicitly by the deployment
 * module rather than through provider auto-discovery.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ForyJsonSerializer implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    /**
     * Holder so that {@code fory-json} is only loaded when this provider is actually used, which
     * lets the dependency stay optional for applications that don't enable JSON support.
     */
    private static final class Holder {
        static final ForyJson INSTANCE = ForyJson.builder().build();
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupportedMediaType(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        // fory-json exposes no InputStream-based reader: JsonReader scans by index, so the whole
        // document has to be resident. Request size is bounded by quarkus.http.limits.max-body-size.
        byte[] bytes = entityStream.readAllBytes();
        if (bytes.length == 0) {
            return null;
        }
        // Deserialize against the declared generic type, so List<Foo> and friends round-trip.
        return getForyJson().fromJson(bytes, TypeRef.of(genericType != null ? genericType : type));
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupportedMediaType(mediaType);
    }

    @Override
    public void writeTo(Object obj, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        // Writes through Fory's pooled buffer straight to the stream, instead of allocating a
        // fresh byte[] per response. Serializes against the runtime class, as JSON writers do.
        getForyJson().writeJsonTo(obj, entityStream);
    }

    private boolean isSupportedMediaType(MediaType mediaType) {
        return mediaType != null && mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE);
    }

    private ForyJson getForyJson() {
        return Holder.INSTANCE;
    }
}
