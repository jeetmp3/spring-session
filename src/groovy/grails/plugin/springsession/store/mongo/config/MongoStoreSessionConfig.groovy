package grails.plugin.springsession.store.mongo.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.Module.SetupContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import grails.plugin.springsession.config.SpringSessionConfigProperties
import grails.plugin.springsession.converters.JdkDeserializer
import grails.plugin.springsession.enums.Serializer
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.serializer.support.DeserializingConverter
import org.springframework.core.serializer.support.SerializingConverter
import org.springframework.session.data.mongo.AbstractMongoSessionConverter
import org.springframework.session.data.mongo.JacksonMongoSessionConverter
import org.springframework.session.data.mongo.JdkMongoSessionConverter
import org.springframework.session.data.mongo.config.annotation.web.http.MongoHttpSessionConfiguration
import org.springframework.util.ClassUtils

import java.util.logging.Logger

/**
 * @author Jitendra Singh
 */
@Configuration
class MongoStoreSessionConfig extends MongoHttpSessionConfiguration {

    private static Logger logger = Logger.getLogger(MongoStoreSessionConfig.class.name)

    final MongoStoreConfigProperties mongoProperties
    final SpringSessionConfigProperties configProperties
    final GrailsApplication grailsApplication

    public MongoStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        mongoProperties = new MongoStoreConfigProperties(config)
        configProperties = new SpringSessionConfigProperties(config)
        this.grailsApplication = grailsApplication
        init()
    }

    private void init() {
        this.setCollectionName(mongoProperties.collectionName)
        setMaxInactiveIntervalInSeconds(configProperties.maxInactiveInterval)
    }

    private List<Module> getJackson2Modules() {
        List<Module> modules = Collections.emptyList();
        if (mongoProperties.jacksonModules) {
            modules = new ArrayList<>(mongoProperties.jacksonModules.size())
            mongoProperties.jacksonModules.collect {
                try {
                    Class<?> cls = ClassUtils.forName(it, this.class.classLoader)
                    Object instance = cls.newInstance();
                    if (instance != null && instance instanceof Module) {
                        modules.add((Module) instance)
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
        return modules
    }

    @Bean
    public AbstractMongoSessionConverter mongoSessionConverter() {
        switch (configProperties.defaultSerializer) {
            case Serializer.JSON:
                List<Module> modules = new ArrayList<>()
                modules.add(new EnableDefaultTypingModule())
                modules.addAll(getJackson2Modules())
                return new JacksonMongoSessionConverter(modules)
            default:
                Converter<byte[], Object> deserializer = new DeserializingConverter(new JdkDeserializer(grailsApplication.classLoader, false))
                return new JdkMongoSessionConverter(new SerializingConverter(), deserializer)
        }
    }

    public class EnableDefaultTypingModule extends SimpleModule {

        @Override
        void setupModule(SetupContext context) {
            ObjectMapper mapper = context.getOwner();
            if (mapper != null) {
                mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            }
        }
    }
}
