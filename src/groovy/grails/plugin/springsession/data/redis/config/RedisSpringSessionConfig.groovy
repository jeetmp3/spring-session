package grails.plugin.springsession.data.redis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.CookieHttpSessionStrategy
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.session.web.http.HttpSessionStrategy
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo

import javax.servlet.http.HttpServletResponse

/**
 * @author Jitendra Singh
 */
@Configuration
public class RedisSpringSessionConfig extends RedisHttpSessionConfiguration {

    List<Map> sentinalNodes
    String sentinalMasterName
    String sentinalPassword

    String hostName
    int port
    String connectionPassword
    int timeout
    boolean usePool
    int dbIndex
    boolean convertPipelineAndTxResults
    String sessionStrategy
    String sessionHeaderName
    String sessionCookieName

    public RedisSpringSessionConfig(ConfigObject config) {
        init(config)
    }

    private void init(ConfigObject conf) {
        sentinalNodes = conf.redis.sentinel.nodes as List<Map>
        sentinalMasterName = conf.redis.sentinel.master ?: ""
        usePool = conf.redis.connectionFactory.usePool ?: false
        hostName = conf.redis.connectionFactory.hostName
        connectionPassword = conf.redis.connectionFactory.password
        port = conf.redis.connectionFactory.port
        sentinalPassword = conf.redis.sentinel.password ?: null
        timeout = conf.redis.sentinel.timeout ?: 5000
        dbIndex = conf.redis.connectionFactory.dbIndex
        convertPipelineAndTxResults = conf.redis.connectionFactory.convertPipelineAndTxResults
        sessionStrategy = conf.strategy.defaultStrategy
        sessionHeaderName = conf.strategy.httpHeader.headerName
        sessionCookieName = conf.strategy.cookie.name
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
        if (sentinalMasterName && sentinalNodes) {
            connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(sentinalNodes, sentinalMasterName), jedisPoolConfig)
            connectionFactory.setShardInfo(shardInfo(hostName, port, sentinalPassword, timeout))
        } else {
            connectionFactory = new JedisConnectionFactory()
            connectionFactory.hostName = hostName
            connectionFactory.port = port
            connectionFactory.timeout = timeout
            connectionFactory.database = dbIndex
            connectionFactory.poolConfig = jedisPoolConfig
            if (connectionPassword) {
                connectionFactory.password = connectionPassword
            }
            connectionFactory.convertPipelineAndTxResults = convertPipelineAndTxResults
        }
        connectionFactory.usePool = usePool
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
        if (sessionStrategy == "HEADER") {
            HeaderHttpSessionStrategy sessionStrategy = new HeaderHttpSessionStrategy()
            sessionStrategy.setHeaderName(sessionHeaderName)
            return sessionStrategy
        } else {
            CookieSerializer cookieSerializer = new DefaultCookieSerializer()
            cookieSerializer.setDomainName(sessionCookieName)
            CookieHttpSessionStrategy sessionStrategy = new CookieHttpSessionStrategy()
            sessionStrategy.setCookieSerializer(cookieSerializer)
        }
    }

    @Bean
    public NoOpConfigureRedisAction configureRedisAction() {
        return new NoOpConfigureRedisAction()
    }
}