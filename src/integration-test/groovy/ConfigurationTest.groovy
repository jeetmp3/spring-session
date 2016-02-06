import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import redis.clients.jedis.JedisShardInfo

/**
 * @author jitendra on 2/10/15.
 */
class ConfigurationTest extends IntegrationSpec {
    @Autowired
    JedisConnectionFactory redisConnectionFactory
    @Autowired
    GrailsApplication grailsApplication
    @Autowired
    JedisShardInfo shardInfo

    void setup() {

    }

    void "Canary Test"() {
        expect:
        true;
    }

    void "Test ShardInfo"(){
        expect:
        shardInfo
    }

    def "Test Connection Factory"() {

        expect:
        redisConnectionFactory
    }
//    @Test
//    void "Test Redis connection Factory"() {
//        expect:
//        grailsApplication.mainContext.getBean("redisConnectionFactory")
//    }

    void cleanup() {

    }
}
