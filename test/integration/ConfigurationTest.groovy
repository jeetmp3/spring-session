import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author jitendra on 2/10/15.
 */
class ConfigurationTest extends IntegrationSpec {

    def redisConnectionFactory
    GrailsApplication grailsApplication

    void setup() {

    }

    void "Canary Test"() {
        expect:
        true;
    }

    def "Test Connection Factory"() {

        expect:
        redisConnectionFactory
    }

    void cleanup() {

    }
}
