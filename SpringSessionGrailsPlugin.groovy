import grails.plugin.springsession.config.SpringSessionConfig
import grails.plugin.springsession.store.jdbc.config.JdbcStoreSessionConfig
import grails.plugin.webxml.FilterManager
import org.springframework.web.filter.DelegatingFilterProxy

class SpringSessionGrailsPlugin {
    def version = "1.2.2-RC1"
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
    def grailsApplication

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
        ConfigObject config = application.config.springsession
        springSessionConfig(SpringSessionConfig) {}
        sessionStoreConfiguration(JdbcStoreSessionConfig, ref("grailsApplication"), config)
    }
}
