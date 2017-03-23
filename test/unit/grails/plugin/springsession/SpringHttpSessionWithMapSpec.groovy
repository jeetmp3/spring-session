package grails.plugin.springsession

import grails.plugin.springsession.converters.LazyDeserializationObject
import org.springframework.data.redis.serializer.RedisSerializer
import spock.lang.Specification

import javax.servlet.http.HttpSession

class SpringHttpSessionWithMapSpec extends Specification {

	HttpSession httpSession = new FakeHttpSession()
	RedisSerializer<Object> redisSerializer = GroovyMock()
	SessionProxy sessionProxy = GroovyStub(){
		getSession() >> httpSession
	}
	SpringHttpSession springHttpSession = new SpringHttpSession()

	def setup(){
		springHttpSession.setSessionProxy(sessionProxy)
		springHttpSession.setRedisSerializer(redisSerializer)
		springHttpSession.setLazyDeserialization(false)
	}

	def "increment value"(){
		given:
		springHttpSession.i = 0
		for(int i=0; i<10; i++){
			springHttpSession.i++
		}

		expect:
		springHttpSession.i == 10
	}

	def "concatenate string"(){
		given:
		springHttpSession.text = ""
		springHttpSession.text += "a"
		springHttpSession.text += "b"
		springHttpSession.text += "c"

		expect:
		springHttpSession.text == "abc"
	}

	def "setAttribute must wrap only complex object with LazyDeserializationObject"(){
		setup:
		springHttpSession.setLazyDeserialization(true)

		when:
		springHttpSession.setAttribute("name", value)

		then:
		result.equals(httpSession.getAttribute("name"))

		where:
		value     				|| result
		1       				|| 1
		"abc" 			        || "abc"
		"abc${1}" 			    || "abc${1}"
		'a' 			        || 'a'
		1.5       				|| 1.5
		true      				|| true
		Collections.emptyList() || new LazyDeserializationObject(Collections.emptyList())

	}

}
