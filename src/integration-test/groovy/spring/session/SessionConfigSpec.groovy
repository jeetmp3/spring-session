package spring.session

import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import org.springframework.session.data.redis.RedisOperationsSessionRepository
import org.springframework.session.web.http.SessionRepositoryFilter
import spock.lang.*

@Integration
class SessionConfigSpec extends Specification {

    SessionRepositoryFilter springSessionRepositoryFilter
    RedisOperationsSessionRepository sessionRepository
    GrailsApplication grailsApplication

    def setup() {
    }

    def cleanup() {
    }

    void "Session Repository Filter bean injected"() {
        expect: "fix me"
        springSessionRepositoryFilter
        sessionRepository
    }

    void "Check http session timeout"() {
        expect:
            sessionRepository.properties.defaultMaxInactiveIntervalInSeconds == 1800
    }
}
