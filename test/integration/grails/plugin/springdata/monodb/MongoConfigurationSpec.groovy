package grails.plugin.springdata.monodb

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.session.ExpiringSession
import org.springframework.session.data.mongo.JdkMongoSessionConverter
import org.springframework.session.data.mongo.MongoOperationsSessionRepository
import spock.lang.Stepwise

/**
 * @author Jitendra Singh.
 */
@Stepwise
class MongoConfigurationSpec extends IntegrationSpec {

//    def mongoSessionConverter
//    MongoOperationsSessionRepository mongoSessionRepository
//    GrailsApplication grailsApplication
//
//    def "mongoSessionRepository must present"() {
//
//        expect:
//        mongoSessionConverter
//        mongoSessionConverter instanceof JdkMongoSessionConverter
//        mongoSessionRepository
//    }
//
//    def "session must be created"() {
//        ExpiringSession session = mongoSessionRepository.createSession()
//        expect:
//        session
//        session.maxInactiveIntervalInSeconds == grailsApplication.config.springsession.maxInactiveInterval
//    }
}
