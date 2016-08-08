package grails.plugin.springsession.store.jdbc.config;

import grails.plugin.springsession.config.SpringSessionConfigProperties;
import grails.plugin.springsession.converters.JdkDeserializer;
import grails.plugin.springsession.store.jdbc.convertor.Jackson2Deserializer;
import grails.plugin.springsession.store.jdbc.convertor.Jackson2Serializer;
import groovy.util.ConfigObject;
import org.apache.commons.dbcp2.BasicDataSource;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.jdbc.config.annotation.web.http.JdbcHttpSessionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static grails.plugin.springsession.utils.ApplicationUtils.objectMapper;

/**
 * @author Jitendra Singh.
 */
@Configuration
public class JdbcStoreSessionConfig extends JdbcHttpSessionConfiguration {

    private final JdbcStoreConfigProperties jdbcConfigProperties;
    private final SpringSessionConfigProperties configProperties;
    private final GrailsApplication grailsApplication;

    public JdbcStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        jdbcConfigProperties = new JdbcStoreConfigProperties(config);
        configProperties = new SpringSessionConfigProperties(config);
        setTableName(jdbcConfigProperties.getTableName());
        setMaxInactiveIntervalInSeconds(configProperties.getMaxInactiveInterval());
        this.grailsApplication = grailsApplication;
    }

    @Bean
    public DataSource springSessionDataSource () {
        BasicDataSource basicDataSource = new BasicDataSource();
        System.out.println(jdbcConfigProperties);
        basicDataSource.setDriverClassName(jdbcConfigProperties.getDriverClassName());
        basicDataSource.setUrl(jdbcConfigProperties.getUrl());
        basicDataSource.setUsername(jdbcConfigProperties.getUsername());
        basicDataSource.setPassword(jdbcConfigProperties.getPassword());

        basicDataSource.setMaxTotal(jdbcConfigProperties.getMaxTotal());
        basicDataSource.setMinIdle(jdbcConfigProperties.getMinIdle());
        basicDataSource.setMaxWaitMillis(jdbcConfigProperties.getMaxWaitMillis());
        basicDataSource.setDefaultAutoCommit(jdbcConfigProperties.getDefaultAutoCommit());
        basicDataSource.setDefaultReadOnly(jdbcConfigProperties.getDefaultReadOnly());
        basicDataSource.setDefaultTransactionIsolation(jdbcConfigProperties.getDefaultTransactionIsolation());
        basicDataSource.setValidationQuery(jdbcConfigProperties.getValidationQuery());
        return basicDataSource;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ConversionService springSessionConversionService() {
        GenericConversionService conversionService = new GenericConversionService();
        switch (configProperties.getDefaultSerializer()) {
            case JSON:
                SerializingConverter serializingConverter = new SerializingConverter(new Jackson2Serializer(objectMapper()));
                DeserializingConverter deserializingConverter = new DeserializingConverter(new Jackson2Deserializer(objectMapper()));
                conversionService.addConverter(Object.class, byte[].class, serializingConverter);
                conversionService.addConverter(byte[].class, Object.class, deserializingConverter);
                break;
            default:
                Deserializer<Object> deserializer = new JdkDeserializer(grailsApplication.getClassLoader(), false);
                conversionService.addConverter(Object.class, byte[].class, new SerializingConverter());
                conversionService.addConverter(byte[].class, Object.class, new DeserializingConverter(deserializer));
                break;
        }
        return conversionService;
    }
}
