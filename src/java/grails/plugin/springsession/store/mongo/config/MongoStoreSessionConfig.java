package grails.plugin.springsession.store.mongo.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import grails.plugin.springsession.config.SpringSessionConfigProperties;
import grails.plugin.springsession.converters.JdkDeserializer;
import grails.plugin.springsession.utils.ApplicationUtils;
import grails.plugin.springsession.utils.Objects;
import groovy.util.ConfigObject;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;
import org.springframework.session.data.mongo.JacksonMongoSessionConverter;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.session.data.mongo.config.annotation.web.http.MongoHttpSessionConfiguration;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jitendra Singh
 */
@Configuration
class MongoStoreSessionConfig extends MongoHttpSessionConfiguration {

    private static Logger logger = Logger.getLogger(MongoStoreSessionConfig.class.getName());

    final MongoStoreConfigProperties mongoProperties;
    final SpringSessionConfigProperties configProperties;
    final GrailsApplication grailsApplication;

    public MongoStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        mongoProperties = new MongoStoreConfigProperties(config);
        configProperties = SpringSessionConfigProperties.getInstance(config);
        this.grailsApplication = grailsApplication;
        init();
    }

    private void init() {
        this.setCollectionName(mongoProperties.getCollectionName());
        setMaxInactiveIntervalInSeconds(configProperties.getMaxInactiveInterval());
    }

    @Bean
    public AbstractMongoSessionConverter mongoSessionConverter() {
        switch (configProperties.getDefaultSerializer()) {
            case JSON:
                List<Module> modules = new ArrayList<Module>();
                modules.add(new EnableDefaultTypingModule());
                modules.addAll(ApplicationUtils.getJackson2Modules(mongoProperties.getJacksonModules(), this.getClass().getClassLoader()));
                return new JacksonMongoSessionConverter(modules);
            default:
                Converter<byte[], Object> deserializer = new DeserializingConverter(new JdkDeserializer(grailsApplication.getClassLoader(), false));
                return new JdkMongoSessionConverter(new SerializingConverter(), deserializer);
        }
    }

    public class EnableDefaultTypingModule extends SimpleModule {

        @Override
        public void setupModule(SetupContext context) {
            ObjectMapper mapper = context.getOwner();
            if (mapper != null) {
                mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            }
        }
    }
}
