package grails.plugin.springsession.data.redis.config

import grails.plugin.springsession.enums.SessionStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.*
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo

/**
 * @author Jitendra Singh
 */
@Configuration
public class RedisSpringSessionConfig extends RedisHttpSessionConfiguration {

    RedisConfigProperties properties

    public RedisSpringSessionConfig(ConfigObject config) {
        this.properties = new RedisConfigProperties(config)
    }

    protected MasterNamedNode masterNamedNode(String name) {
        MasterNamedNode masterName = new MasterNamedNode()
        masterName.name = name
        return masterName;
    }

    protected JedisShardInfo shardInfo(String hostName, int port, String sentinalPassword, int timeout) {
        JedisShardInfo shardInfo = new JedisShardInfo(hostName ?: "localhost", port ?: 6379)
        shardInfo.password = sentinalPassword ?: null
        shardInfo.timeout = timeout ?: 5000
        return shardInfo
    }

    protected RedisSentinelConfiguration redisSentinelConfiguration(List<Map> nodes, String masterName) {
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration()
        configuration.setMaster(masterNamedNode(masterName))
        configuration.setSentinels(
                (nodes.collect { new RedisNode(it.host as String, it.port as Integer) }) as Set
        )
        return configuration
    }

    @Bean
    public JedisPoolConfig poolConfig() {
        return new JedisPoolConfig();
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory connectionFactory = null;
        if (properties.sentinalMasterName && properties.sentinalNodes) {
            connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(properties.sentinalNodes, properties.sentinalMasterName), jedisPoolConfig)
            connectionFactory.setShardInfo(shardInfo(properties.hostName, properties.port, properties.sentinalPassword, properties.timeout))
        } else {
            connectionFactory = new JedisConnectionFactory()
            connectionFactory.hostName = properties.hostName
            connectionFactory.port = properties.port
            connectionFactory.timeout = properties.timeout
            connectionFactory.database = properties.dbIndex
            connectionFactory.poolConfig = jedisPoolConfig
            if (properties.connectionPassword) {
                connectionFactory.password = properties.connectionPassword
            }
            connectionFactory.convertPipelineAndTxResults = properties.convertPipelineAndTxResults
        }
        connectionFactory.usePool = properties.usePool
        return connectionFactory
    }

    @Bean
    public RedisTemplate sessionRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                              RedisSerializer defaultSerializer) {
        RedisTemplate template = new RedisTemplate()
        template.setDefaultSerializer(defaultSerializer)
        template.setConnectionFactory(redisConnectionFactory)
        return template
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        if (properties.sessionStrategy == SessionStrategy.HEADER) {
            HeaderHttpSessionStrategy sessionStrategy = new HeaderHttpSessionStrategy()
            sessionStrategy.setHeaderName(properties.sessionHeaderName)
            return sessionStrategy
        } else {
            CookieSerializer cookieSerializer = new DefaultCookieSerializer()
            cookieSerializer.setDomainName(properties.sessionCookieName)
            CookieHttpSessionStrategy sessionStrategy = new CookieHttpSessionStrategy()
            sessionStrategy.setCookieSerializer(cookieSerializer)
        }
    }

    @Bean
    public NoOpConfigureRedisAction configureRedisAction() {
        return new NoOpConfigureRedisAction()
    }
}