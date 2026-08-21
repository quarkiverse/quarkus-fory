package io.quarkiverse.fory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.apache.fory.json.ForyJson;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;

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

    private ForyJson foryJson;

    public ForyJsonSerializer() {
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupportedMediaType(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        byte[] bytes = entityStream.readAllBytes();
        if (bytes.length == 0) {
            return null;
        }
        return getForyJson().fromJson(bytes, type);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupportedMediaType(mediaType);
    }

    @Override
    public void writeTo(Object obj, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        if (obj == null) {
            entityStream.write("null".getBytes(StandardCharsets.UTF_8));
            return;
        }
        byte[] bytes = getForyJson().toJsonBytes(obj);
        entityStream.write(bytes);
    }

    private boolean isSupportedMediaType(MediaType mediaType) {
        return mediaType != null && mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE);
    }

    private ForyJson getForyJson() {
        if (foryJson == null) {
            ArcContainer container = Arc.container();
            if (container != null) {
                foryJson = container.instance(ForyJson.class).get();
            }
        }
        return foryJson;
    }
}
