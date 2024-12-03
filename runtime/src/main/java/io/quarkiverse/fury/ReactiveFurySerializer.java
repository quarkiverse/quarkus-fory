package io.quarkiverse.fury;

import java.io.IOException;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import org.apache.fury.io.FuryInputStream;
import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyReader;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyWriter;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;

public class ReactiveFurySerializer extends FurySerializer
        implements ServerMessageBodyReader<Object>, ServerMessageBodyWriter<Object> {
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final ResteasyReactiveResourceInfo lazyMethod,
            final MediaType mediaType) {
        return isSupportedMediaType(mediaType) && canSerialize(type);
    }

    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final MediaType mediaType,
            final ServerRequestContext context) throws WebApplicationException, IOException {
        return getFury().deserialize(new FuryInputStream(context.getInputStream()));
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final ResteasyReactiveResourceInfo target,
            final MediaType mediaType) {
        return isSupportedMediaType(mediaType) && canSerialize(type);
    }

    @Override
    public void writeResponse(final Object obj, final Type genericType, final ServerRequestContext context)
            throws WebApplicationException, IOException {
        context.getOrCreateOutputStream().write(getFury().serialize(obj));
    }
}
