import grails.test.spock.IntegrationSpec
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory

/**
 * @author jitendra on 2/10/15.
 */
class ConfigurationTest extends IntegrationSpec {

    JedisConnectionFactory redisConnectionFactory

    void setup() {

    }

    void "Canary Test"() {
        expect:
        true;
    }

    def "Test Connection Factory"() {
        expect:
        redisConnectionFactory
        redisConnectionFactory.hostName == "localhost"
        redisConnectionFactory.port == 6379
        redisConnectionFactory.timeout == 2000
        redisConnectionFactory.usePool
    }

    void cleanup() {

    }
}
