package grails.plugin.springsession.store.jdbc.config

import java.sql.Connection

/**
 * @author Jitendra Singh.
 */
class JdbcStoreConfigProperties {

    String driverClassName
    String url
    String username
    String password
    String tableName

    Integer maxActive
    Integer maxTotal
    Integer minIdle
    long maxWaitMillis
    boolean defaultAutoCommit
    boolean defaultReadOnly
    int defaultTransactionIsolation
    String validationQuery

    public JdbcStoreConfigProperties(ConfigObject config) {
        driverClassName = config.jdbc.driverClassName ?: "org.h2.Driver"
        url = config.jdbc.url ?: "jdbc:h2:~/test"
        username = config.jdbc.username ?: ""
        password = config.jdbc.password ?: ""
        tableName = config.jdbc.tableName ?: "SessionData"

        maxActive = config.jdbc.pool.maxActive ?: 10
        maxTotal = config.jdbc.pool.maxTotal ?: 20
        minIdle = config.jdbc.pool.minIdle ?: 2
        maxWaitMillis = config.jdbc.pool.maxWaitMillis ?: 10000
        defaultAutoCommit = config.jdbc.pool.defaultAutoCommit as Boolean
        defaultReadOnly = config.jdbc.pool.defaultAutoCommit ?: false
        defaultTransactionIsolation = config.jdbc.pool.defaultTransactionIsolation ?: Connection.TRANSACTION_READ_COMMITTED
        validationQuery = config.jdbc.pool.validationQuery ?: "SELECT 1"
    }


    @Override
    public String toString() {
        return this.dump();
    }
}
