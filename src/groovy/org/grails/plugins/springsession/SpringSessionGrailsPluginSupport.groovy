package org.grails.plugins.springsession

import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.grails.plugins.springsession.converters.GrailsJdkSerializationRedisSerializer
import org.grails.plugins.springsession.converters.LazyDeserializationRedisSerializer
import org.grails.plugins.springsession.data.redis.RedisLogoutHandler
import org.grails.plugins.springsession.data.redis.RedisSecurityContextRepository
import org.grails.plugins.springsession.data.redis.SecurityContextDao
import org.grails.plugins.springsession.data.redis.config.MasterNamedNode
import org.grails.plugins.springsession.data.redis.config.NoOpConfigureRedisAction
import org.grails.plugins.springsession.scope.SpringSessionBeanFactoryPostProcessor
import org.grails.plugins.springsession.scope.SpringSessionScope
import org.grails.plugins.springsession.web.http.HttpSessionSynchronizer
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.session.data.redis.RedisFlushMode
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.session.web.http.CookieHttpSessionStrategy
import org.springframework.session.web.http.DefaultCookieSerializer
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.web.filter.DelegatingFilterProxy
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo

class SpringSessionGrailsPluginSupport {
	static Closure doWithWebDescriptor = { xml ->
		def contextParams = xml.'context-param'
		contextParams[contextParams.size() - 1] + {
			filter {
				'filter-name'('springSessionRepositoryFilter')
				'filter-class'(DelegatingFilterProxy.name)
			}
		}

		contextParams[contextParams.size() - 1] + {
			filter {
				'filter-name'('httpSessionSynchronizer')
				'filter-class'(DelegatingFilterProxy.name)
			}
		}

		def filter = xml.'filter'

		filter[filter.size() - 1] + {
			'filter-mapping' {
				'filter-name'('httpSessionSynchronizer')
				'url-pattern'('/*')
				dispatcher('ERROR')
				dispatcher('REQUEST')
			}
		}

		filter[filter.size() - 1] + {
			'filter-mapping' {
				'filter-name'('springSessionRepositoryFilter')
				'url-pattern'('/*')
				dispatcher('ERROR')
				dispatcher('REQUEST')
				dispatcher('FORWARD')
			}
		}
	}

	static Closure doWithSpring = { GrailsApplication application, GrailsPlugin plugin ->
		mergeConfig(application)
		ConfigObject conf = application.config.springsession

		if (conf.enabled as Boolean) {
			println "\nConfiguring Spring Session..."

			// JDK Serializer bean
			jdkSerializationRedisSerializer(GrailsJdkSerializationRedisSerializer, application.getClassLoader())
			lazyDeserializationRedisSerializer(LazyDeserializationRedisSerializer, application.getClassLoader())
			stringRedisSerializer(StringRedisSerializer)

			poolConfig(JedisPoolConfig) {}
			if (conf.redis.sentinel.master && conf.redis.sentinel.nodes) {
				List<Map> nodes = conf.redis.sentinel.nodes as List<Map>
				masterName(MasterNamedNode) {
					name = conf.redis.sentinel.master
				}
				shardInfo(JedisShardInfo, conf.redis.connectionFactory.hostName, conf.redis.connectionFactory.port) {
					password = conf.redis.sentinel.password ?: null
					timeout = conf.redis.sentinel.timeout ?: 5000
				}
				redisSentinelConfiguration(RedisSentinelConfiguration) {
					master = ref("masterName")
					sentinels = (nodes.collect { new RedisNode(it.host as String, it.port as Integer) }) as Set
				}
				redisConnectionFactory(JedisConnectionFactory, ref("redisSentinelConfiguration"), ref("poolConfig")) {
					shardInfo = ref("shardInfo")
					usePool = conf.redis.connectionFactory.usePool
				}
			} else {
				// Redis Connection Factory Default is JedisConnectionFactory
				redisConnectionFactory(JedisConnectionFactory) {
					hostName = conf.redis.connectionFactory.hostName ?: "localhost"
					port = conf.redis.connectionFactory.port ?: 6379
					timeout = conf.redis.connectionFactory.timeout ?: 2000
					usePool = conf.redis.connectionFactory.usePool
					database = conf.redis.connectionFactory.dbIndex
					poolConfig = ref("poolConfig")
					if (conf.redis.connectionFactory.password) {
						password = conf.redis.connectionFactory.password
					}
					convertPipelineAndTxResults = conf.redis.connectionFactory.convertPipelineAndTxResults
				}
			}

			sessionRedisTemplate(RedisTemplate) { bean ->
				keySerializer = ref("stringRedisSerializer")
				hashKeySerializer = ref("stringRedisSerializer")
				connectionFactory = ref("redisConnectionFactory")
				if (conf.lazy.deserialization as Boolean) {
					defaultSerializer = ref("lazyDeserializationRedisSerializer")
				} else {
					defaultSerializer = ref("jdkSerializationRedisSerializer")
				}
				bean.initMethod = "afterPropertiesSet"
			}

			String defaultStrategy = conf.strategy.defaultStrategy
			if (defaultStrategy == "HEADER") {
				httpSessionStrategy(HeaderHttpSessionStrategy) {
					headerName = conf.strategy.httpHeader.headerName
				}
			} else {
				cookieSerializer(DefaultCookieSerializer) {
					cookieName = conf.strategy.cookie.name
					cookiePath = conf.strategy.cookie.path
					useSecureCookie = conf.strategy.cookie.useSecure
					domainNamePattern = conf.strategy.cookie.domainNamePattern
				}

				httpSessionStrategy(CookieHttpSessionStrategy) {
					cookieSerializer = ref("cookieSerializer")
				}
			}

			redisHttpSessionConfiguration(RedisHttpSessionConfiguration) {
				maxInactiveIntervalInSeconds = conf.maxInactiveInterval
				httpSessionStrategy = ref("httpSessionStrategy")
				redisFlushMode = RedisFlushMode.ON_SAVE
			}

			configureRedisAction(NoOpConfigureRedisAction)
			httpSessionSynchronizer(HttpSessionSynchronizer) {
				persistMutable = conf.allow.persist.mutable as Boolean
			}

			"${conf.beanName}"(SpringHttpSession) {
				lazyDeserialization = conf.lazy.deserialization as Boolean
				redisSerializer = ref("lazyDeserializationRedisSerializer")
				sessionProxy = new GrailsSessionProxy()
			}

			if (conf.isolate.securityContext as Boolean) {
				securityContextDao(SecurityContextDao) {
					redisTemplate = ref("sessionRedisTemplate")
				}

				redisSecurityContextRepository(RedisSecurityContextRepository) {
					securityContextDao = ref("securityContextDao")
				}

				redisLogoutHandler(RedisLogoutHandler) {
					redisSecurityContextRepository = ref("redisSecurityContextRepository")
				}

				securityContextPersistenceFilter(SecurityContextPersistenceFilter) {
					securityContextRepository = ref("redisSecurityContextRepository")
				}
			}

			springSessionBeanFactoryPostProcessor(SpringSessionBeanFactoryPostProcessor)

			println "... finished configuring Spring Session"
		}
	}

	static Closure doWithApplicationContext = { ApplicationContext applicationContext ->
		try {
			RedisLogoutHandler redisLogoutHandler = applicationContext.getBean('redisLogoutHandler')
			applicationContext.logoutHandlers.add(0, redisLogoutHandler)
		} catch (BeansException ignored) {
		}
	}

	private static void mergeConfig(GrailsApplication grailsApplication) {
		ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
		ConfigObject configObject = configSlurper.parse(grailsApplication.classLoader.loadClass('DefaultSessionConfig'))
		configObject.merge(grailsApplication.config)
		grailsApplication.config = configObject
	}
}
