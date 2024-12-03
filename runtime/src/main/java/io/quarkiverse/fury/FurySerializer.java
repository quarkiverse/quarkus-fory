package io.quarkiverse.fury;

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
import jakarta.ws.rs.ext.Provider;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.io.FuryInputStream;
import org.apache.fury.resolver.ClassResolver;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;

@Provider
@Consumes({ "application/fury", "application/*+fury" })
@Produces({ "application/fury", "application/*+fury" })
public class FurySerializer implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
    private BaseFury fury;

    public FurySerializer() {
    }

    @Override
    public boolean isReadable(final Class<?> aClass, final Type type, final Annotation[] annotations,
            final MediaType mediaType) {
        return isSupportedMediaType(mediaType) && canSerialize(aClass);
    }

    @Override
    public Object readFrom(final Class<Object> aClass, final Type type, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, String> multivaluedMap, final InputStream inputStream)
            throws WebApplicationException {
        return getFury().deserialize(new FuryInputStream(inputStream));
    }

    @Override
    public boolean isWriteable(final Class<?> aClass, final Type type, final Annotation[] annotations,
            final MediaType mediaType) {
        return isSupportedMediaType(mediaType) && canSerialize(aClass);
    }

    @Override
    public void writeTo(final Object obj, final Class<?> aClass, final Type type, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream)
            throws IOException, WebApplicationException {
        outputStream.write(getFury().serialize(obj));
    }

    protected boolean isSupportedMediaType(MediaType mediaType) {
        return mediaType.getType().equals("application") && mediaType.getSubtype().endsWith("fury");
    }

    protected boolean canSerialize(final Class<?> aClass) {
        if (getFury() instanceof final ThreadSafeFury threadSafeFury) {
            return (threadSafeFury).execute(f -> f.getClassResolver().getRegisteredClassId(aClass)) != null;
        } else {
            ClassResolver classResolver = ((Fury) getFury()).getClassResolver();
            return classResolver.getRegisteredClassId(aClass) != null;
        }
    }

    protected BaseFury getFury() {
        if (fury == null) {
            ArcContainer container = Arc.container();
            if (container != null) {
                fury = container.instance(BaseFury.class).get();
            }
        }
        return fury;
    }

}
