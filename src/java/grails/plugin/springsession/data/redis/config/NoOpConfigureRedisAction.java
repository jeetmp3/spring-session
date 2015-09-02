package grails.plugin.springsession.data.redis.config;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

/**
 * Created by jitendra on 1/7/15.
 */
public class NoOpConfigureRedisAction implements ConfigureRedisAction {
    @Override
    public void configure(RedisConnection redisConnection) {
    }
}
