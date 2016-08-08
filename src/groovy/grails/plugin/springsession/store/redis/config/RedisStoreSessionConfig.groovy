package grails.plugin.springsession.store.redis.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import grails.plugin.springsession.config.SpringSessionConfig
import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer
import grails.plugin.springsession.enums.Serializer
import grails.plugin.springsession.redis.config.MasterNamedNode
import grails.plugin.springsession.redis.config.NoOpConfigureRedisAction
import grails.plugin.springsession.utils.ApplicationUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo

import javax.annotation.PostConstruct
import java.util.logging.Logger

/**
 * @author Jitendra Singh
 */
@Configuration
public class RedisStoreSessionConfig extends RedisHttpSessionConfiguration {

    RedisStoreConfigProperties properties
    GrailsApplication grailsApplication
    private final Serializer defaultSerializer
    private Logger logger = Logger.getLogger(RedisStoreSessionConfig.class.name)

    public RedisStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        this.grailsApplication = grailsApplication
        this.properties = new RedisStoreConfigProperties(config)
        defaultSerializer = config.defaultSerializer ?: Serializer.JDK

        int maxInactiveInterval = config.maxInactiveInterval ?: 1800
        setMaxInactiveIntervalInSeconds(maxInactiveInterval)
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
        connectionFactory.afterPropertiesSet()
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
    public NoOpConfigureRedisAction configureRedisAction() {
        return new NoOpConfigureRedisAction()
    }

    @Bean
    public RedisSerializer<? extends Object> springSessionDefaultRedisSerializer() {
        switch (defaultSerializer) {
            case Serializer.JSON:
                Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer<Object>(Object.class)
                serializer.setObjectMapper(ApplicationUtils.objectMapper())
                return serializer
                break
            default:
                return new GrailsJdkSerializationRedisSerializer(grailsApplication);
        }
    }
}