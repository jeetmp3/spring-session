import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.grails.plugins.springsession.converters.GrailsJdkSerializationRedisSerializer
import org.grails.plugins.springsession.data.redis.config.MasterNamedNode
import org.grails.plugins.springsession.data.redis.config.NoOpConfigureRedisAction
import org.grails.plugins.springsession.web.http.HttpSessionSynchronizer
import org.grails.plugins.springsession.config.SpringSessionConfig
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.CookieHttpSessionStrategy
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo
import utils.SpringSessionUtils

@Slf4j
class SpringSessionGrailsPlugin extends Plugin {

    def version = "2.0.0-SNAPSHOT"
    def grailsVersion = "3.0.0 > *"
    def title = "Spring Session Grails Plugin"
    def author = "Jitendra Singh"
    def authorEmail = "jeet.mp3@gmail.com"
    def description = 'Provides support for SpringSession project'
    def documentation = "https://github.com/jeetmp3/spring-session"
    def license = "APACHE"
    def issueManagement = [url: "https://github.com/jeetmp3/spring-session/issues"]
    def scm = [url: "https://github.com/jeetmp3/sprinrequest.getSession()g-session"]
    def loadAfter = ['springSecurityCore', 'cors']
    def profiles = ['web']

    Closure doWithSpring() {
        { ->
            println "\n++++++ Configuring Spring session"
            SpringSessionUtils.application = grailsApplication
            ConfigObject conf = SpringSessionUtils.sessionConfig

            springSessionConfig(SpringSessionConfig) {
                grailsApplication = grailsApplication
            }

            if (conf.redis.sentinel.master && conf.redis.sentinel.nodes) {
                List<Map> nodes = conf.redis.sentinel.nodes as List<Map>
                masterName(MasterNamedNode) {
                    name = conf.redis.sentinel.master
                }
                shardInfo(JedisShardInfo, conf.redis.connectionFactory.hostName, conf.redis.connectionFactory.port) {
                    password = conf.redis.sentinel.password ?: null
                    timeout = conf.redis.sentinel.timeout ?: 5000
                }
                redisSentinelConfiguration(RedisSentinelConfiguration) {
                    master = ref("masterName")
                    sentinels = (nodes.collect { new RedisNode(it.host as String, it.port as Integer) }) as Set
                }
                redisConnectionFactory(JedisConnectionFactory, ref("redisSentinelConfiguration"), ref("poolConfig")) {
                    shardInfo = ref("shardInfo")
                    usePool = conf.redis.connectionFactory.usePool
                }
            } else {
                // Redis Connection Factory Default is JedisConnectionFactory
                redisConnectionFactory(JedisConnectionFactory) {
                    hostName = conf.redis.connectionFactory.hostName ?: "localhost"
                    port = conf.redis.connectionFactory.port ?: 6379
                    timeout = conf.redis.connectionFactory.timeout ?: 2000
                    usePool = conf.redis.connectionFactory.usePool
                    database = conf.redis.connectionFactory.dbIndex
                    if (conf.redis.connectionFactory.password) {
                        password = conf.redis.connectionFactory.password
                    }
                    convertPipelineAndTxResults = conf.redis.connectionFactory.convertPipelineAndTxResults
                }
            }

            sessionRedisTemplate(RedisTemplate) { bean ->
                keySerializer = ref("stringRedisSerializer")
                hashKeySerializer = ref("stringRedisSerializer")
                connectionFactory = ref("redisConnectionFactory")
                defaultSerializer = ref("jdkSerializationRedisSerializer")
                bean.initMethod = "afterPropertiesSet"
            }

            String defaultStrategy = conf.strategy.defaultStrategy
            if (defaultStrategy == "HEADER") {
                httpSessionStrategy(HeaderHttpSessionStrategy) {
                    headerName = conf.strategy.httpHeader.headerName
                }
            } else {
                httpSessionStrategy(CookieHttpSessionStrategy) {
                    cookieName = conf.strategy.cookie.name
                }
            }

//            redisHttpSessionConfiguration(RedisHttpSessionConfiguration) {
//                maxInactiveIntervalInSeconds = conf.maxInactiveIntervalInSeconds
//                httpSessionStrategy = ref("httpSessionStrategy")
//            }

            configureRedisAction(NoOpConfigureRedisAction)
            httpSessionSynchronizer(HttpSessionSynchronizer) {
                persistMutable = conf.allow.persist.mutable as Boolean
            }

            println "++++++ Finished Spring Session configuration"
        }
    }
}
