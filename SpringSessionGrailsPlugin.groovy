import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer
import grails.plugin.springsession.data.redis.config.MasterNamedNode
import grails.plugin.springsession.data.redis.config.NoOpConfigureRedisAction
import grails.plugin.springsession.web.http.HttpSessionSynchronizer
import grails.plugin.webxml.FilterManager
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.CookieHttpSessionStrategy
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.web.filter.DelegatingFilterProxy
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo

class SpringSessionGrailsPlugin {
    def version = "1.2"
    def grailsVersion = "2.4 > *"
    def title = "Spring Session Grails Plugin"
    def author = "Jitendra Singh"
    def authorEmail = "jeet.mp3@gmail.com"
    def description = 'Provides support for SpringSession project'
    def documentation = "https://github.com/jeetmp3/spring-session"
    def license = "APACHE"
    def issueManagement = [url: "https://github.com/jeetmp3/spring-session/issues"]
    def scm = [url: "https://github.com/jeetmp3/sprinrequest.getSession()g-session"]
    def loadAfter = ['springSecurityCore', 'cors']

    def getWebXmlFilterOrder() {
        FilterManager filterManager = getClass().getClassLoader().loadClass('grails.plugin.webxml.FilterManager')
        return [springSessionRepositoryFilter: filterManager.CHAR_ENCODING_POSITION - 2,
                httpSessionSynchronizer: filterManager.CHAR_ENCODING_POSITION - 1]
    }

    def doWithWebDescriptor = { xml ->
        def contextParams = xml.'context-param'
        contextParams[contextParams.size() - 1] + {
            filter {
                'filter-name'('springSessionRepositoryFilter')
                'filter-class'(DelegatingFilterProxy.name)
            }
        }

        contextParams[contextParams.size() - 1] + {
            filter {
                'filter-name'('httpSessionSynchronizer')
                'filter-class'(DelegatingFilterProxy.name)
            }
        }

//        def filterMapping = xml.'filter-mapping'
        def filter = xml.'filter'

        filter[filter.size() - 1] + {
            'filter-mapping' {
                'filter-name'('httpSessionSynchronizer')
                'url-pattern'('/*')
                dispatcher('ERROR')
                dispatcher('REQUEST')
            }
        }

        filter[filter.size() - 1] + {
            'filter-mapping' {
                'filter-name'('springSessionRepositoryFilter')
                'url-pattern'('/*')
                dispatcher('ERROR')
                dispatcher('REQUEST')
                dispatcher('FORWARD')
            }
        }
    }

    def doWithSpring = {
        println "\n++++++ Configuring Spring session"
        mergeConfig(application)
        ConfigObject conf = application.config.springsession

        // JDK Serializer bean
        jdkSerializationRedisSerializer(GrailsJdkSerializationRedisSerializer, ref('grailsApplication'))
        stringRedisSerializer(StringRedisSerializer)

        poolConfig(JedisPoolConfig) {}
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

        redisHttpSessionConfiguration(RedisHttpSessionConfiguration) {
            maxInactiveIntervalInSeconds = conf.maxInactiveInterval
            httpSessionStrategy = ref("httpSessionStrategy")
        }

        configureRedisAction(NoOpConfigureRedisAction)
        httpSessionSynchronizer(HttpSessionSynchronizer) {
            persistMutable = conf.allow.persist.mutable as Boolean
        }

        println "++++++ Finished Spring Session configuration"
    }

    def configureRedis = { ConfigObject conf ->

    }

    private void mergeConfig(GrailsApplication grailsApplication) {
        ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
        ConfigObject configObject = configSlurper.parse(grailsApplication.classLoader.loadClass("DefaultSessionConfig"))
        configObject.merge(grailsApplication.config)
        grailsApplication.config = configObject
    }
}
