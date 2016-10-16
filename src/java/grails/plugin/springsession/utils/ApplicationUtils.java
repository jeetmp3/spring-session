package grails.plugin.springsession.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jitendra Singh.
 */
public class ApplicationUtils {

    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    public static List<Module> getJackson2Modules(List<String> moduleClasses, ClassLoader classLoader) {
        List<Module> modules = Collections.emptyList();
        if (!Objects.isEmpty(moduleClasses)) {
            modules = new ArrayList<Module>(moduleClasses.size());
            for (String module : moduleClasses) {
                try {
                    Class<?> cls = ClassUtils.forName(module, classLoader);
                    Object instance = cls.newInstance();
                    if (instance != null && instance instanceof Module) {
                        modules.add((Module) instance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return modules;
    }


    public static byte[] toByteArray(String string) {
        if (string != null && string.length() > 0) {
            return string.getBytes();
        }
        return null;
    }

    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean notEmpty(String object) {
        return notNull(object) && object.length() > 0;
    }
}
