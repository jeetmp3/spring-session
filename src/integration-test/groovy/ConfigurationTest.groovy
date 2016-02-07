import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
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

    void "Canary Test"() {
        expect:
        true;
    }

//    void "Test ShardInfo"() {
//        expect:
//        shardInfo
//    }
//
//    def "Test Connection Factory"() {
//
//        expect:
//        redisConnectionFactory
//    }

    void cleanup() {

    }
}
