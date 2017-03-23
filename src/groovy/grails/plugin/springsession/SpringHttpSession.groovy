package grails.plugin.springsession

import grails.plugin.springsession.converters.LazyDeserializationObject
import grails.plugin.springsession.utils.TypeUtils
import org.springframework.data.redis.serializer.RedisSerializer

import javax.servlet.ServletContext
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionContext

class SpringHttpSession implements HttpSession {

    private boolean lazyDeserialization
    private RedisSerializer<Object> redisSerializer
    private SessionProxy sessionProxy


    HttpSession obtainSession() {
        sessionProxy.getSession()
    }

    public Object getAttribute(String name, Closure defaultValueFactory = null) {
        HttpSession session = obtainSession()
        Object value = session.getAttribute(name)
        if (value == null && defaultValueFactory != null) {
            value = defaultValueFactory.call()
        }
        // Save the object in the session if:
        // 1) It was just created
        // 2) It was already existing, but may be changed during this request. Doing this, it makes sure that it'll be updated in Redis
        if (value != null) {
            setAttribute(name, value)
        }

        if(value instanceof LazyDeserializationObject){
            LazyDeserializationObject lazyDeserializationObject = (LazyDeserializationObject) value
            if(lazyDeserializationObject.deserializaded == null){
                lazyDeserializationObject.deserializaded = redisSerializer.deserialize(lazyDeserializationObject.serialized)
            }
            return lazyDeserializationObject.deserializaded
        }

        return value
    }

    public void setAttribute(String name, Object value) {
        HttpSession session = obtainSession()

        if(!lazyDeserialization || TypeUtils.isNullOrWrapperType(value)){
            session.setAttribute(name, value)
        }
        else {
            LazyDeserializationObject lazyDeserializationObject
            if(value instanceof LazyDeserializationObject){
                lazyDeserializationObject = value
            }
            else {
                lazyDeserializationObject = new LazyDeserializationObject(value)
            }

            session.setAttribute(name, lazyDeserializationObject)
        }
    }

    @Override
    Object getProperty(String name){
        return getAttribute(name)
    }

    @Override
    void setProperty(String name, Object value){
        setAttribute(name, value)
    }

    @Override
    long getCreationTime() {
        HttpSession session = obtainSession()
        return session.getCreationTime()
    }

    @Override
    String getId() {
        HttpSession session = obtainSession()
        return session.getId()
    }

    @Override
    long getLastAccessedTime() {
        HttpSession session = obtainSession()
        return session.getLastAccessedTime()
    }

    @Override
    ServletContext getServletContext() {
        HttpSession session = obtainSession()
        return session.getServletContext()
    }

    @Override
    void setMaxInactiveInterval(int interval) {
        HttpSession session = obtainSession()
        session.setMaxInactiveInterval(interval)
    }

    @Override
    int getMaxInactiveInterval() {
        HttpSession session = obtainSession()
        return session.getMaxInactiveInterval()
    }

    @Override
    HttpSessionContext getSessionContext() {
        HttpSession session = obtainSession()
        return session.getSessionContext()
    }

    @Override
    Object getValue(String name) {
        return getAttribute(name)
    }

    @Override
    Enumeration<String> getAttributeNames() {
        HttpSession session = obtainSession()
        return session.getAttributeNames()
    }

    @Override
    String[] getValueNames() {
        HttpSession session = obtainSession()
        return session.getValueNames()
    }


    @Override
    void putValue(String name, Object value) {
        setAttribute(name, value)
    }

    @Override
    void removeAttribute(String name) {
        HttpSession session = obtainSession()
        session.removeAttribute(name)
    }

    @Override
    void removeValue(String name) {
        removeAttribute(name)
    }

    @Override
    void invalidate() {
        HttpSession session = obtainSession()
        session.invalidate()
    }

    @Override
    boolean isNew() {
        HttpSession session = obtainSession()
        return session.isNew()
    }

    void setLazyDeserialization(boolean lazyDeserialization) {
        this.lazyDeserialization = lazyDeserialization
    }

    void setRedisSerializer(RedisSerializer<Object> redisSerializer) {
        this.redisSerializer = redisSerializer
    }

    void setSessionProxy(SessionProxy sessionObtainer) {
        this.sessionProxy = sessionObtainer
    }
}
