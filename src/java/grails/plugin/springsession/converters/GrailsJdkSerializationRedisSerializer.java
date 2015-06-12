package grails.plugin.springsession.converters;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

/**
 * Created by jitendra on 28/5/15.
 */
public class GrailsJdkSerializationRedisSerializer implements RedisSerializer<Object> {

    private Converter<Object, byte[]> serializer;
    private Converter<byte[], Object> deserializer;
    private GrailsApplication grailsApplication;

    public GrailsJdkSerializationRedisSerializer(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
        serializer = new SerializingConverter();
        deserializer = new DeserializingConverter(new JdkDeserializer(grailsApplication.getClassLoader(), false));
        Assert.notNull(this.grailsApplication);
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        } else {
            try {
                return this.serializer.convert(object);
            } catch (Exception ex) {
                throw new SerializationException("Cannot serialize", ex);
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        } else {
            try {
                return this.deserializer.convert(bytes);
            } catch (Exception ex) {
                throw new SerializationException("Cannot deserialize", ex);
            }
        }
    }


}
