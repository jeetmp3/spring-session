package org.grails.plugins.springsession.config;

import grails.core.GrailsApplication;
import org.grails.plugins.springsession.converters.GrailsJdkSerializationRedisSerializer;
import org.grails.plugins.springsession.web.http.HttpSessionSynchronizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.PoolConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.ExpiringSession;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.SessionRepositoryFilter;
import redis.clients.jedis.JedisPoolConfig;

@EnableRedisHttpSession
public class SpringSessionConfig {
    private GrailsApplication grailsApplication;

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    @Bean
    public RedisSerializer jdkSerializationRedisSerializer() {
        return new GrailsJdkSerializationRedisSerializer(grailsApplication);
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public JedisPoolConfig poolConfig() {
        return new JedisPoolConfig();
    }

    @Bean
    public FilterRegistrationBean springSessionFilter(SessionRepositoryFilter<? extends ExpiringSession> filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registrationBean;
    }

//    @Bean
//    public RedisOperationsSessionRepository sessionRepository(RedisTemplate<String, ExpiringSession> sessionRedisTemplate) {
//        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(sessionRedisTemplate);
//        sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
//        return sessionRepository;
//    }

    @Bean
    public FilterRegistrationBean sessionSynchronizerFilter(HttpSessionSynchronizer filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 11);
        return registrationBean;
    }
}
