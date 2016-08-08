package grails.plugin.springsession.store.jdbc.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.serializer.Deserializer;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jitendra Singh.
 */
public class Jackson2Deserializer implements Deserializer<Object> {

    final ObjectMapper objectMapper;

    public Jackson2Deserializer(ObjectMapper mapper) {
        Assert.notNull(mapper, "ObjectMapper must not be null");
        objectMapper = mapper;
    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        if(inputStream != null){
            return objectMapper.readValue(inputStream, Object.class);
        }
        return null;
    }
}
