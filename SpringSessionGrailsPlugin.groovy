import grails.plugin.webxml.FilterManager
import org.grails.plugins.springsession.SpringSessionGrailsPluginSupport

class SpringSessionGrailsPlugin {
	def version = "1.4-SNAPSHOT"
	def grailsVersion = "2.2.4 > *"
	def title = "Spring Session Grails Plugin"
	def author = "Jitendra Singh"
	def authorEmail = "jeet.mp3@gmail.com"
	def description = 'Provides support for SpringSession project'
	def documentation = "https://github.com/zgsolucoes/spring-session"
	def license = "APACHE"
	def issueManagement = [url: "https://github.com/zgsolucoes/spring-session/issues"]
	def scm = [url: "https://github.com/zgsolucoes/spring-session"]
	def loadAfter = ['springSecurityCore', 'cors']

	def getWebXmlFilterOrder() {
		FilterManager filterManager = getClass().getClassLoader().loadClass('grails.plugin.webxml.FilterManager')
		return [
				springSessionRepositoryFilter: filterManager.CHAR_ENCODING_POSITION - 2,
				httpSessionSynchronizer      : filterManager.CHAR_ENCODING_POSITION - 1
		]
	}

	def doWithWebDescriptor = { xml ->
		SpringSessionGrailsPluginSupport.doWithWebDescriptor.delegate = delegate
		SpringSessionGrailsPluginSupport.doWithWebDescriptor xml
	}

	def doWithSpring = {
		SpringSessionGrailsPluginSupport.doWithSpring.delegate = delegate
		SpringSessionGrailsPluginSupport.doWithSpring application, plugin

	}

	def doWithApplicationContext = { applicationContext ->
		SpringSessionGrailsPluginSupport.doWithApplicationContext.delegate = delegate
		SpringSessionGrailsPluginSupport.doWithApplicationContext applicationContext
	}

	def configureRedis = { ConfigObject conf ->
	}
}
