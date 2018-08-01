package utils

import grails.core.GrailsApplication
import grails.util.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySource

import static org.springframework.util.ObjectUtils.isEmpty

class SpringSessionUtils {

    public static GrailsApplication application
    protected static ConfigObject config

    static ConfigObject getSessionConfig() {
        if(isEmpty(config)) {
            loadConfig((application.config.springsession ?: new ConfigObject()) as ConfigObject)
        }
        return (config)
    }

    private static void loadConfig(ConfigObject currentConfig) {
        ConfigObject defaultConfig = loadDefaultConfig("DefaultSessionConfig")
        mergeConfig(defaultConfig, currentConfig)
    }

    private static ConfigObject loadDefaultConfig(String className) {
        ConfigObject defaultConfig = new ConfigSlurper(Environment.current.name).parse(
                new GroovyClassLoader(this.classLoader).loadClass(className)
        )
        return (defaultConfig?.springsession ?: new ConfigObject())
    }

    private static void mergeConfig(ConfigObject defaultConfig, ConfigObject currentConfig) {
        config = defaultConfig.merge(currentConfig) as ConfigObject
        ConfigObject mergedConfig = new ConfigObject()
        mergedConfig.springsession = config
        PropertySource propertySource = new MapPropertySource('SessionConfig', mergedConfig)
        MutablePropertySources propertySources = application.mainContext.environment.propertySources
        propertySources.addFirst(propertySource)
        application.config.springsession = config
        if(!config.active){
            // set the store type to none to disable session auto-configuration
            PropertySource<Properties> systemProperties = (PropertySource<Properties>) propertySources.get('systemProperties')
            systemProperties.source.put('spring.session.store-type', 'none')
        }
    }
}
