package grails.plugin.springsession.store.hazelcast.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import grails.plugin.springsession.config.SpringSessionConfigProperties;
import groovy.util.ConfigObject;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.HazelcastHttpSessionConfiguration;

/**
 * @author Jitendra Singh.
 */
@Configuration
public class HazelcastStoreSessionConfig extends HazelcastHttpSessionConfiguration {

    private final GrailsApplication grailsApplication;
    private final SpringSessionConfigProperties configProperties;

    public HazelcastStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        this.grailsApplication = grailsApplication;
        this.configProperties = new SpringSessionConfigProperties(config);
        this.setMaxInactiveIntervalInSeconds(configProperties.getMaxInactiveInterval());
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }
}
