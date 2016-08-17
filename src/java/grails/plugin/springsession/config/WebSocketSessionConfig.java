package grails.plugin.springsession.config;

import groovy.util.ConfigObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * @author Jitendra Singh.
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketSessionConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    WebSocketSessionConfigProperties properties;

    public WebSocketSessionConfig(ConfigObject config) {
        properties = new WebSocketSessionConfigProperties(config);
    }

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(properties.getStompEndpoints().toArray(new String[]{}));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(properties.getSimpleBrokers().toArray(new String[]{}));
        registry.setApplicationDestinationPrefixes(properties.getAppDestinationPrefix().toArray(new String[]{}));
    }
}
