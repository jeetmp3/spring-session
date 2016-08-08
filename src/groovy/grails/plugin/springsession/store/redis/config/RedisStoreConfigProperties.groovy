package grails.plugin.springsession.store.redis.config

import grails.plugin.springsession.enums.SessionStrategy

/**
 * @author Jitendra Singh.
 */
class RedisStoreConfigProperties {

    List<Map> sentinalNodes
    String sentinalMasterName
    String sentinalPassword

    String hostName
    int port
    String connectionPassword
    int timeout
    boolean usePool
    int dbIndex
    boolean convertPipelineAndTxResults
    SessionStrategy sessionStrategy
    String sessionHeaderName
    String sessionCookieName

    public RedisStoreConfigProperties(ConfigObject conf) {
        sentinalNodes = conf.redis.sentinel.nodes as List<Map>
        sentinalMasterName = conf.redis.sentinel.master ?: ""
        usePool = conf.redis.connectionFactory.usePool ?: false
        hostName = conf.redis.connectionFactory.hostName
        connectionPassword = conf.redis.connectionFactory.password ?: ''
        port = conf.redis.connectionFactory.port
        sentinalPassword = conf.redis.sentinel.password ?: null
        timeout = conf.redis.sentinel.timeout ?: 5000
        dbIndex = conf.redis.connectionFactory.dbIndex
        convertPipelineAndTxResults = conf.redis.connectionFactory.convertPipelineAndTxResults
        sessionStrategy = conf.strategy.defaultStrategy ?: SessionStrategy.COOKIE
        sessionHeaderName = conf.strategy.httpHeader.headerName
        sessionCookieName = conf.strategy.cookie.name
    }

}
