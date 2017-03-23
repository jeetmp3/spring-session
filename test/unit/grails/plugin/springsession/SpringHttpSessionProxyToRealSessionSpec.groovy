package grails.plugin.springsession

import org.springframework.data.redis.serializer.RedisSerializer
import spock.lang.Specification

import javax.servlet.http.HttpSession

class SpringHttpSessionProxyToRealSessionSpec extends Specification {

	HttpSession httpSession = GroovyMock()
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

	def "setAttribute redirected to grails session"(){
		when:
		springHttpSession.setAttribute(name, value)

		then:
		1 * httpSession.setAttribute(name, value)

		where:
		name = "name"
		value = "value"
	}

	def "getAttribute redirected to grails session"(){
		when:
		springHttpSession.getAttribute(name)

		then:
		1 * httpSession.getAttribute(name)

		where:
		name = "name"
	}

	def "getAttribute calls setAttribute if value is not null"(){
		when:
		springHttpSession.getAttribute(name)

		then:
		1 * httpSession.getAttribute(name) >> value
		1 * httpSession.setAttribute(name, value)

		where:
		name = "name"
		value = "value"
	}

	def "getAttribute calls setAttribute with default value from closure"(){
		when:
		springHttpSession.getAttribute(name, {value})

		then:
		1 * httpSession.setAttribute(name, value)

		where:
		name = "name"
		value = "value"
	}

	def "getAttribute doesn't call setAttribute if response is null"(){
		when:
		springHttpSession.getAttribute(name)

		then:
		1 * httpSession.getAttribute(name) >> null
		0 * httpSession.setAttribute(name, value)

		where:
		name = "name"
		value = "value"
	}

	def "indirect getProperty redirected to getAttribute"(){
		when:
		springHttpSession.name

		then:
		1 * httpSession.getAttribute(name)

		where:
		name = "name"
	}

	def "indirect getProperty (map style) redirected to getAttribute"(){
		when:
		springHttpSession[name]

		then:
		1 * httpSession.getAttribute(name)

		where:
		name = "name"
	}

	def "indirect setProperty redirected to setAttribute"(){
		when:
		springHttpSession.name = value

		then:
		1 * httpSession.setAttribute(name, value)

		where:
		name = "name"
		value = "value"
	}

	def "indirect setProperty (map style) redirected to setAttribute"(){
		when:
		springHttpSession[name] = value

		then:
		1 * httpSession.setAttribute(name, value)

		where:
		name = "name"
		value = "value"
	}

	def "getAttribute for the first time sets the default value"(){
		when:
		def result = springHttpSession.getAttribute(name, {value})

		then:
		1 * httpSession.setAttribute(name, value)
		result == value

		where:
		name = "name"
		value = "value"
	}

}
