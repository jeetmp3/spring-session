package grails.plugin.springsession.config

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * @author Jitendra Singh.
 */
class SpringSessionConfigProperties {

    String tableName
    String driverClassName
    String url
    String username
    String password

    int maxTotal
    int maxActive
    int minIdle
    int maxWaitMillis
    boolean defaultAutoCommit
    boolean defaultReadOnly

    int maxInactiveInterval
    String cookieName
    String httpHeaderName
    Boolean allowPersistMutable

    public SpringSessionConfigProperties(ConfigObject config) {
        def jdbcConfig = config.jdbc
        tableName = jdbcConfig.tableName
        driverClassName = jdbcConfig.driverClassName
        url = jdbcConfig.url
        username = jdbcConfig.username
        password = jdbcConfig.password

        def poolConfig = jdbcConfig.pool
        maxActive = poolConfig.maxActive
        maxTotal = poolConfig.maxTotal
        minIdle = poolConfig.minIdle
        maxWaitMillis = poolConfig.maxWaitMillis
        defaultAutoCommit = poolConfig.defaultAutoCommit
        defaultReadOnly = poolConfig.defaultReadOnly

        maxInactiveInterval = config.maxInactiveIntervalInSeconds ?: 1800
        cookieName = config.strategy.cookie.name
        httpHeaderName = config.strategy.httpHeader.headerName
        allowPersistMutable = config.allow.persist.mutable ?: false
    }

    String getDdlScript() {
        String path = "org/springframework/session/jdbc/schema-mysql.sql";
        Resource resource = new ClassPathResource(path);
        resource.inputStream.text
    }
}
