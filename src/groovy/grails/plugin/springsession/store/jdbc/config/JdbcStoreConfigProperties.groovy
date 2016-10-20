package grails.plugin.springsession.store.jdbc.config

import grails.plugin.springsession.utils.Objects
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

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
    String ddlScript
    List<String> jacksonModules

    final String DEFAULT_TABLE_NAME = 'SPRING_SESSION'

    public JdbcStoreConfigProperties(ConfigObject config) {
        ConfigObject jdbc = config.jdbc
        driverClassName = jdbc.driverClassName ?: "org.h2.Driver"
        url = jdbc.url ?: "jdbc:h2:~/test"
        username = jdbc.username ?: ""
        password = jdbc.password ?: ""
        tableName = jdbc.tableName ?: "SessionData"

        maxActive = jdbc.pool.maxActive ?: 10
        maxTotal = jdbc.pool.maxTotal ?: 20
        minIdle = jdbc.pool.minIdle ?: 2
        maxWaitMillis = jdbc.pool.maxWaitMillis ?: 10000
        defaultAutoCommit = jdbc.pool.defaultAutoCommit as Boolean
        defaultReadOnly = jdbc.pool.defaultReadOnly ?: false
        defaultTransactionIsolation = jdbc.pool.defaultTransactionIsolation ?: Connection.TRANSACTION_READ_COMMITTED
        validationQuery = jdbc.pool.validationQuery ?: "SELECT 1"
        if(jdbc.jackson.modules && jdbc.jackson.modules instanceof List)
            jacksonModules = jdbc.jackson.modules

        prepareDDLScript()
    }

    private void prepareDDLScript() {
        String path = "org/springframework/session/jdbc/schema-"+vendor()+".sql";
        Resource resource = new ClassPathResource(path)
        String content = resource.inputStream.text
        ddlScript = content.replaceAll(DEFAULT_TABLE_NAME, tableName)
    }

    private String vendor() {
        String dbUrl = url;
        if (!Objects.isEmpty(dbUrl)) {
            int vendorNameStartIndex = dbUrl.indexOf(':') + 1;
            int vendorNameEndIndex = dbUrl.indexOf(':', vendorNameStartIndex);
            if(vendorNameEndIndex != -1) {
                return dbUrl.substring(vendorNameStartIndex, vendorNameEndIndex);
            }
        }
        return "mysql"
    }

    @Override
    public String toString() {
        return this.dump();
    }
}
