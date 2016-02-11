package org.grails.plugins.springsession.data.redis.config;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

/**
 * @author jitendra
 */
public class NoOpConfigureRedisAction implements ConfigureRedisAction {
    @Override
    public void configure(RedisConnection redisConnection) {
    }
}
