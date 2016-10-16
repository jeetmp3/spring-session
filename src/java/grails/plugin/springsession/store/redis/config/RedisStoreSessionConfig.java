package grails.plugin.springsession.store.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import grails.plugin.springsession.config.SpringSessionConfig;
import grails.plugin.springsession.config.SpringSessionConfigProperties;
import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer;
import grails.plugin.springsession.utils.ApplicationUtils;
import grails.plugin.springsession.utils.Objects;
import groovy.util.ConfigObject;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Jitendra Singh
 */
@Configuration
public class RedisStoreSessionConfig extends RedisHttpSessionConfiguration {

    RedisStoreConfigProperties redisStoreConfigProperties;
    SpringSessionConfigProperties springSessionConfigProperties;
    GrailsApplication grailsApplication;
    private Logger logger = Logger.getLogger(RedisStoreSessionConfig.class.getName());

    public RedisStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        this.grailsApplication = grailsApplication;
        this.redisStoreConfigProperties = new RedisStoreConfigProperties(config);
        this.springSessionConfigProperties = SpringSessionConfigProperties.getInstance(config);
    }

    @PostConstruct
    public void init() {
        setMaxInactiveIntervalInSeconds(springSessionConfigProperties.getMaxInactiveInterval());
    }

    protected MasterNamedNode masterNamedNode(String name) {
        MasterNamedNode masterName = new MasterNamedNode();
        masterName.name = name;
        return masterName;
    }

    protected JedisShardInfo shardInfo(String hostName, int port, String sentinalPassword, int timeout) {
        JedisShardInfo shardInfo = new JedisShardInfo(
                Objects.isEmpty(hostName) ? "localhost" : hostName, port);
        shardInfo.setPassword(sentinalPassword);
        shardInfo.setTimeout(timeout);
        return shardInfo;
    }

    protected RedisSentinelConfiguration redisSentinelConfiguration(List<Map> nodes, String masterName) {
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        configuration.setMaster(masterNamedNode(masterName));
        if (!Objects.isEmpty(nodes)) {
            List<RedisNode> nodeList = new ArrayList<RedisNode>(nodes.size());
            for (Map map : nodes) {
                if (!Objects.isEmpty(map) && map.containsKey("host") && map.containsKey("port")) {
                    nodeList.add(new RedisNode(String.valueOf(map.get("host")), (Integer) map.get("port")));
                }
            }
            configuration.setSentinels(nodeList);
        }
        return configuration;
    }

    @Bean
    public JedisPoolConfig poolConfig() {
        return new JedisPoolConfig();
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory connectionFactory = null;
        if (!Objects.isEmpty(redisStoreConfigProperties.getSentinalMasterName())
                && !Objects.isEmpty(redisStoreConfigProperties.getSentinalNodes())) {
            connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(
                    redisStoreConfigProperties.getSentinalNodes(),
                    redisStoreConfigProperties.getSentinalMasterName()), jedisPoolConfig);
            connectionFactory.setShardInfo(
                    shardInfo(redisStoreConfigProperties.getHostName(),
                            redisStoreConfigProperties.getPort(),
                            redisStoreConfigProperties.getSentinalPassword(),
                            redisStoreConfigProperties.getTimeout()));
        } else {
            connectionFactory = new JedisConnectionFactory();
            connectionFactory.setHostName(redisStoreConfigProperties.getHostName());
            connectionFactory.setPort(redisStoreConfigProperties.getPort());
            connectionFactory.setTimeout(redisStoreConfigProperties.getTimeout());
            connectionFactory.setDatabase(redisStoreConfigProperties.getDbIndex());
            connectionFactory.setPoolConfig(jedisPoolConfig);
            if (!Objects.isEmpty(redisStoreConfigProperties.getConnectionPassword())) {
                connectionFactory.setPassword(redisStoreConfigProperties.getConnectionPassword());
            }
            connectionFactory.setConvertPipelineAndTxResults(redisStoreConfigProperties.getConvertPipelineAndTxResults());
        }
        connectionFactory.setUsePool(redisStoreConfigProperties.getUsePool());
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    @Bean
    public RedisTemplate sessionRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                              RedisSerializer defaultSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(defaultSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public NoOpConfigureRedisAction configureRedisAction() {
        return new NoOpConfigureRedisAction();
    }

    @Bean
    public RedisSerializer<? extends Object> springSessionDefaultRedisSerializer() {
        switch (springSessionConfigProperties.getDefaultSerializer()) {
            case JSON:
                Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
                ObjectMapper mapper = ApplicationUtils.objectMapper();
                mapper.registerModules(ApplicationUtils.getJackson2Modules(redisStoreConfigProperties.getJacksonModules(), this.getClass().getClassLoader()));
                serializer.setObjectMapper(mapper);
                return serializer;
            default:
                return new GrailsJdkSerializationRedisSerializer(grailsApplication);
        }

    }
}