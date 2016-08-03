import grails.plugin.springsession.SpringSessionConfig
import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer
import grails.plugin.springsession.data.redis.config.MasterNamedNode
import grails.plugin.springsession.data.redis.config.NoOpConfigureRedisAction
import grails.plugin.springsession.data.redis.config.RedisSpringSessionConfig
import grails.plugin.springsession.enums.SessionStore
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
    def version = "1.2.1-BUILD_SNAPSHOT"
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
        springSessionConfig(SpringSessionConfig, ref("grailsApplication"), conf.allow.persist.mutable as Boolean) {}

        SessionStore store = conf.sessionStore ?: SessionStore.REDIS

        switch (store) {
            case SessionStore.REDIS:
                redisSpringSessionConfig(RedisSpringSessionConfig, conf) {}
                break
            case SessionStore.MONGO:
                break
        }

        redisHttpSessionConfiguration(RedisHttpSessionConfiguration) {
            maxInactiveIntervalInSeconds = conf.maxInactiveInterval
            httpSessionStrategy = ref("httpSessionStrategy")
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
