package grails.plugin.springsession.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jitendra Singh.
 */
public final class Objects {

    private final static String EMPTY_STRING = "";

    private Objects() {
    }

    public static boolean isEmpty(String string) {
        return isNull(string) || EMPTY_STRING.equals(string);
    }

    public static boolean isEmpty(Collection collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNull(Object object) {
        return object == null;
    }
}
