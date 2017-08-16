package grails.plugin.springsession.store.jdbc.config;

import com.mysql.jdbc.Connection;
import grails.plugin.springsession.config.SpringSessionConfigProperties;
import grails.plugin.springsession.converters.JdkDeserializer;
import groovy.util.ConfigObject;
import org.apache.commons.dbcp2.BasicDataSource;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.session.jdbc.config.annotation.web.http.JdbcHttpSessionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jitendra Singh.
 */
@Configuration
public class JdbcStoreSessionConfig extends JdbcHttpSessionConfiguration {

    private final GrailsApplication grailsApplication;
    private final SpringSessionConfigProperties springSessionConfigProperties;

    public JdbcStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        this.springSessionConfigProperties = new SpringSessionConfigProperties(config);
        this.grailsApplication = grailsApplication;
        setTableName(springSessionConfigProperties.getTableName());
        setMaxInactiveIntervalInSeconds(springSessionConfigProperties.getMaxInactiveInterval());
    }

    @Bean
    public JdbcTemplate springSessionJdbcOperations(@Qualifier("springSessionDataSource") DataSource dataSource,
                                                    @Qualifier("springSessionConversionService") ConversionService conversionService) throws SQLException {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        createTable(template);
        this.setSpringSessionConversionService(conversionService);
        return template;
    }

    @Bean
    public DataSource springSessionDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(springSessionConfigProperties.getDriverClassName());
        basicDataSource.setUrl(springSessionConfigProperties.getUrl());
        basicDataSource.setUsername(springSessionConfigProperties.getUsername());
        basicDataSource.setPassword(springSessionConfigProperties.getPassword());

        basicDataSource.setMaxTotal(springSessionConfigProperties.getMaxTotal());
        basicDataSource.setMinIdle(springSessionConfigProperties.getMinIdle());
        basicDataSource.setMaxWaitMillis(springSessionConfigProperties.getMaxWaitMillis());
        basicDataSource.setDefaultAutoCommit(springSessionConfigProperties.getDefaultAutoCommit());
        basicDataSource.setDefaultReadOnly(springSessionConfigProperties.getDefaultReadOnly());
        basicDataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        basicDataSource.setValidationQuery("SELECT 1");
        return basicDataSource;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(@Qualifier("springSessionDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ConversionService springSessionConversionService() {
        GenericConversionService conversionService = new GenericConversionService();
        Deserializer<Object> deserializer = new JdkDeserializer(grailsApplication.getClassLoader(), false);
        conversionService.addConverter(Object.class, byte[].class, new SerializingConverter());
        conversionService.addConverter(byte[].class, Object.class, new DeserializingConverter(deserializer));
        return conversionService;
    }

    private void createTable(JdbcTemplate template) throws SQLException {
        java.sql.Connection connection = template.getDataSource().getConnection();
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "SPRING_SESSION", null);
        if (!rs.next()) {
            ScriptUtils.executeSqlScript(connection, new ByteArrayResource(springSessionConfigProperties.getDdlScript().getBytes()));
        }
    }
}
