package grails.plugin.springsession.store.jdbc.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.serializer.Serializer;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jitendra Singh.
 */
public class Jackson2Serializer implements Serializer<Object> {

    final ObjectMapper objectMapper;

    public Jackson2Serializer(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "objectMapper must not be null");
        this.objectMapper = objectMapper;
    }

    @Override
    public void serialize(Object object, OutputStream outputStream) throws IOException {
        if (object != null) {
            objectMapper.writeValue(outputStream, object);
        }
    }
}
