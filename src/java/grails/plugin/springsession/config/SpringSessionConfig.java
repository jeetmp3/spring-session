package grails.plugin.springsession.config;

import grails.plugin.springsession.web.http.HttpSessionSynchronizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionStrategy;

/**
 * @author Jitendra Singh
 */
@Configuration("springSessionConfig")
public class SpringSessionConfig {

    private static final String COOKIE_NAME = "SESSION";

    public SpringSessionConfig() {
    }

    @Bean
    public RedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public HttpSessionSynchronizer httpSessionSynchronizer() {
        HttpSessionSynchronizer synchronizer = new HttpSessionSynchronizer();
        synchronizer.setPersistMutable(false);
        return synchronizer;
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName(COOKIE_NAME);
        CookieHttpSessionStrategy sessionStrategy = new CookieHttpSessionStrategy();
        sessionStrategy.setCookieSerializer(cookieSerializer);
        return sessionStrategy;

    }
}
