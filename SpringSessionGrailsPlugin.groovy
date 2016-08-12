import grails.plugin.springsession.config.SpringSessionConfig
import grails.plugin.springsession.enums.SessionStore
import grails.plugin.springsession.store.hazelcast.config.HazelcastStoreSessionConfig
import grails.plugin.springsession.store.jdbc.config.JdbcStoreSessionConfig
import grails.plugin.springsession.store.mongo.config.MongoStoreSessionConfig
import grails.plugin.springsession.store.mongo.config.MongoStoreSpringDataConfig
import grails.plugin.springsession.store.redis.config.RedisStoreSessionConfig
import grails.plugin.webxml.FilterManager
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.session.hazelcast.config.annotation.web.http.HazelcastHttpSessionConfiguration
import org.springframework.web.filter.DelegatingFilterProxy

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
                httpSessionSynchronizer      : filterManager.CHAR_ENCODING_POSITION - 1]
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

        ConfigObject config = application.config.springsession
        SessionStore sessionStore = config.sessionStore ?: SessionStore.REDIS

        springSessionConfig(SpringSessionConfig, ref("grailsApplication"), config) {}

        switch (sessionStore) {
            case SessionStore.JDBC:
                sessionStoreConfiguration(JdbcStoreSessionConfig, ref("grailsApplication"), config)
                break;
            case SessionStore.MONGO:
                mongoSpringDataConfig(MongoStoreSpringDataConfig, config)
                sessionStoreConfiguration(MongoStoreSessionConfig, ref("grailsApplication"), config)
                break;
            case SessionStore.HAZELCAST:
                sessionStoreConfiguration(HazelcastStoreSessionConfig, ref("grailsApplication"), config)
                break;
            default:
                sessionStoreConfiguration(RedisStoreSessionConfig, ref("grailsApplication"), config)
                break;
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
