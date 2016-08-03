package grails.plugin.springsession;

import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer;
import grails.plugin.springsession.web.http.HttpSessionSynchronizer;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Jitendra Singh
 */
@Configuration
public class SpringSessionConfig {

    GrailsApplication grailsApplication;
    boolean persistMutable;

    public SpringSessionConfig(GrailsApplication grailsApplication, boolean persistMutable) {
        this.grailsApplication = grailsApplication;
        this.persistMutable = persistMutable;
    }

    public SpringSessionConfig(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    @Bean
    public GrailsJdkSerializationRedisSerializer defaultSerializer() {
        return new GrailsJdkSerializationRedisSerializer(grailsApplication);
    }

    @Bean
    public RedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public HttpSessionSynchronizer httpSessionSynchronizer() {
        HttpSessionSynchronizer synchronizer = new HttpSessionSynchronizer();
        synchronizer.setPersistMutable(persistMutable);
        return synchronizer;
    }
}
