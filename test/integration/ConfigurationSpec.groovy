import grails.plugin.springsession.converters.GrailsJdkSerializationRedisSerializer
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.session.web.http.CookieHttpSessionStrategy

/**
 * @author jitendra on 2/10/15.
 */
class ConfigurationSpec extends IntegrationSpec {

    GrailsApplication grailsApplication
    def httpSessionStrategy
    def springSessionDefaultRedisSerializer
    def sessionRepository
    def redisConnectionFactory

    void setup() {
    }

//    def "Test Connection Factory"() {
//        println(grailsApplication.config.springsession.strategy.defaultStrategy)
//        expect:
//        httpSessionStrategy
//        httpSessionStrategy instanceof CookieHttpSessionStrategy
//        sessionRepository
//        springSessionDefaultRedisSerializer instanceof  GrailsJdkSerializationRedisSerializer
//        redisConnectionFactory.hostName == "localhost"
//        redisConnectionFactory.port == 6379
//        redisConnectionFactory.timeout == 2000
//        redisConnectionFactory.usePool
//    }

    void cleanup() {

    }
}
