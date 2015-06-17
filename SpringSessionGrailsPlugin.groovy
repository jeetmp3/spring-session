import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.web.filter.DelegatingFilterProxy

class SpringSessionGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/controllers",
            "grails-app/views"
    ]

    // TODO Fill in these fields
    def title = "Spring Session Grails Plugin" // Headline display name of the plugin
    def author = "Jitendra Singh"
    def authorEmail = "jeet.mp3@gmail.com"
    def description = '''\
Spring Session Grails Plugin provides support for SpringSession project available in Grails.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/jeetmp3/spring-session"
//    def documentation = "http://grails.org/plugin/spring-session"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/jeetmp3/spring-session" ]

    def loadAfter = ['springSecurityCore', 'cors']

    def getWebXmlFilterOrder() {
        [springSessionRepositoryFilter: FilterManager.GRAILS_WEB_REQUEST_POSITION - 1]
    }

    def doWithWebDescriptor = { xml ->
        def contextParams = xml.'context-param'
        contextParams[contextParams.size() - 1] + {
            filter {
                'filter-name'('springSessionRepositoryFilter')
                'filter-class'(DelegatingFilterProxy.name)
            }
        }

        def filterMapping = xml.'filter-mapping'
        filterMapping[filterMapping.size() - 1] + {
            'filter-mapping' {
                'filter-name'('springSessionRepositoryFilter')
                'url-pattern'('/*')
                dispatcher('ERROR')
                dispatcher('REQUEST')
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

        println "++++++ Finishing Spring Session configuration"
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    private void mergeConfig(GrailsApplication grailsApplication) {
        ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
        ConfigObject configObject = configSlurper.parse(grailsApplication.classLoader.loadClass("DefaultSessionConfig"))
        configObject.merge(grailsApplication.config)
        grailsApplication.config = configObject
    }
}
