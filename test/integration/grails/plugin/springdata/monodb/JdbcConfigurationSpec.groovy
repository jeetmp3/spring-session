package grails.plugin.springdata.monodb

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.session.ExpiringSession
import org.springframework.session.jdbc.JdbcOperationsSessionRepository

import javax.sql.DataSource

/**
 * @author Jitendra Singh.
 */
class JdbcConfigurationSpec extends IntegrationSpec {
    def springSessionConversionService
    JdbcOperationsSessionRepository sessionRepository
    GrailsApplication grailsApplication
    DataSource dataSource
    def transactionManager

//    def "jdbcSessionRepository must present"() {
//
//        expect:
//        springSessionConversionService
//        springSessionConversionService instanceof GenericConversionService
//        sessionRepository
//        dataSource
//        transactionManager
//    }
//
//    def "session must be created"() {
//        ExpiringSession session = sessionRepository.createSession()
//        expect:
//        session
//        session.maxInactiveIntervalInSeconds == grailsApplication.config.springsession.maxInactiveInterval
//    }
}
