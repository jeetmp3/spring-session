package org.grails.plugins.springsession.converters;

import org.springframework.core.serializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * @author jitendra
 */
public class JdkDeserializer implements Deserializer<Object> {

    private ClassLoader classLoader;
    private Boolean instantiate;

    public JdkDeserializer(ClassLoader classLoader, Boolean instantiate) {
        this.classLoader = classLoader;
        this.instantiate = instantiate;
    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), instantiate, classLoader);
                } catch (ClassNotFoundException e) {
                    try {
                        return super.resolveClass(desc);
                    } catch (ClassNotFoundException e2) {
                        throw e;
                    }
                }
            }
        };
        try {
            return objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
