package grails.plugin.springsession.config;

import grails.plugin.springsession.enums.SessionStrategy;
import grails.plugin.springsession.web.http.HttpSessionSynchronizer;
import groovy.util.ConfigObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import java.util.logging.Logger;

/**
 * @author Jitendra Singh
 */
@Configuration
public class SpringSessionConfig {

    private Logger logger = Logger.getLogger(SpringSessionConfig.class.getName());

    private SpringSessionConfigProperties configProperties;

    public SpringSessionConfig(ConfigObject config) {
        this.configProperties = new SpringSessionConfigProperties(config);
    }

    @Bean
    public RedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public HttpSessionSynchronizer httpSessionSynchronizer() {
        HttpSessionSynchronizer synchronizer = new HttpSessionSynchronizer();
        synchronizer.setPersistMutable(configProperties.getAllowPersistMutable());
        return synchronizer;
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        if (configProperties.getDefaultSessionStrategy() == SessionStrategy.HEADER) {
            HeaderHttpSessionStrategy sessionStrategy = new HeaderHttpSessionStrategy();
            sessionStrategy.setHeaderName(configProperties.getHttpHeaderName());
            return sessionStrategy;
        } else {
            DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
            cookieSerializer.setCookieName(configProperties.getCookieName());
            CookieHttpSessionStrategy sessionStrategy = new CookieHttpSessionStrategy();
            sessionStrategy.setCookieSerializer(cookieSerializer);
            return sessionStrategy;
        }
    }
}
