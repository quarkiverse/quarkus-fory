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
import jakarta.ws.rs.ext.Provider;

import org.apache.fory.BaseFory;
import org.apache.fory.Fory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.io.ForyInputStream;
import org.apache.fory.resolver.ClassResolver;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;

@Provider
@Consumes({ "application/fory", "application/*+fory" })
@Produces({ "application/fory", "application/*+fory" })
public class ForySerializer implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
    private BaseFory fory;

    public ForySerializer() {
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
        return getFory().deserialize(new ForyInputStream(inputStream));
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
        outputStream.write(getFory().serialize(obj));
    }

    protected boolean isSupportedMediaType(MediaType mediaType) {
        return mediaType.getType().equals("application") && mediaType.getSubtype().endsWith("fory");
    }

    protected boolean canSerialize(final Class<?> aClass) {
        Config config = ConfigProvider.getConfig();
        Boolean requiredClassRegistration = config.getValue("quarkus.fory.required-class-registration", Boolean.class);
        if (!requiredClassRegistration) {
            return true;
        }
        if (getFory() instanceof final ThreadSafeFory threadSafeFory) {
            return (threadSafeFory).execute(f -> f.getClassResolver().getRegisteredClassId(aClass)) != null;
        } else {
            ClassResolver classResolver = ((Fory) getFory()).getClassResolver();
            return classResolver.getRegisteredClassId(aClass) != null;
        }
    }

    protected BaseFory getFory() {
        if (fory == null) {
            ArcContainer container = Arc.container();
            if (container != null) {
                fory = container.instance(BaseFory.class).get();
            }
        }
        return fory;
    }

}
