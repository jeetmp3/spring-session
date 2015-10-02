import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer
import grails.plugin.springsession.data.redis.config.NoOpConfigureRedisAction
import grails.plugin.springsession.web.http.HttpSessionSynchronizer
import grails.plugin.webxml.FilterManager
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.web.filter.DelegatingFilterProxy

class SpringSessionGrailsPlugin {
    def version = "1.0"
    def grailsVersion = "2.4 > *"
    def title = "Spring Session Grails Plugin"
    def author = "Jitendra Singh"
    def authorEmail = "jeet.mp3@gmail.com"
    def description = 'Provides support for SpringSession project'
    def documentation = "https://github.com/jeetmp3/spring-session"
    def license = "APACHE"
    def issueManagement = [url: "https://github.com/jeetmp3/spring-session/issues"]
    def scm = [url: "https://github.com/jeetmp3/spring-session"]
    def loadAfter = ['springSecurityCore', 'cors']

    def getWebXmlFilterOrder() {
        [springSessionRepositoryFilter: FilterManager.GRAILS_WEB_REQUEST_POSITION - 1]
    }

    def doWithWebDescriptor = { xml ->
        def contextParams = xml.'context-param'
        contextParams[contextParams.size() - 1] + {
            filter {
                'filter-name'('httpSessionSynchronizer')
                'filter-class'(DelegatingFilterProxy.name)
            }
        }

        contextParams[contextParams.size() - 1] + {
            filter {
                'filter-name'('springSessionRepositoryFilter')
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
        println "++++++ Configuring Spring session"
        mergeConfig(application)
        def conf = application.config.springsession

        // JDK Serializer bean
        jdkSerializationRedisSerializer(GrailsJdkSerializationRedisSerializer, ref('grailsApplication'))
        stringRedisSerializer(StringRedisSerializer)

        // Redis Connection Factory Default is JedisConnectionFactory
        redisConnectionFactory(JedisConnectionFactory) {
            hostName = conf.redis.connectionFactory.hostName ?: "localhost"
            port = conf.redis.connectionFactory.port ?: 6379
            timeout = conf.redis.connectionFactory.timeout ?: 2000
            usePool = conf.redis.connectionFactory.usePool
            convertPipelineAndTxResults = conf.redis.connectionFactory.convertPipelineAndTxResults
        }

        sessionRedisTemplate(RedisTemplate) { bean ->
            keySerializer = ref("stringRedisSerializer")
            hashKeySerializer = ref("stringRedisSerializer")
            connectionFactory = ref("redisConnectionFactory")
            defaultSerializer = ref("jdkSerializationRedisSerializer")
            bean.initMethod = "afterPropertiesSet"
        }

        String defaultStrategy = conf.strategy.defaultStrategy as String
        if (defaultStrategy == "HEADER") {
            httpSessionStrategy(HeaderHttpSessionStrategy)
        }

        redisHttpSessionConfiguration(RedisHttpSessionConfiguration) {
            maxInactiveIntervalInSeconds = conf.maxInactiveInterval
        }

        configureRedisAction(NoOpConfigureRedisAction)
        httpSessionSynchronizer(HttpSessionSynchronizer)

        println "++++++ Finished Spring Session configuration"
    }

    private void mergeConfig(GrailsApplication grailsApplication) {
        ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
        ConfigObject configObject = configSlurper.parse(grailsApplication.classLoader.loadClass("DefaultSessionConfig"))
        configObject.merge(grailsApplication.config)
        grailsApplication.config = configObject
    }
}
