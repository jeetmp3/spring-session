import grails.plugin.springsession.enums.Serializer
import grails.plugin.springsession.enums.SessionStore
import grails.plugin.springsession.enums.SessionStrategy
import org.springframework.session.data.mongo.MongoOperationsSessionRepository

import java.sql.Connection

springsession {
    maxInactiveInterval = 1800
    sessionStore = SessionStore.REDIS
    defaultSerializer = Serializer.JDK
    strategy {
        defaultStrategy = SessionStrategy.COOKIE
        cookie.name = "SESSION"
        httpHeader.headerName = "x-auth-token"
    }
    allow.persist.mutable = false

    websocket {
        stompEndpoints = []
        appDestinationPrefix = []
        simpleBrokers = []
    }

    redis {
        connectionFactory {
            hostName = "localhost"
            port = 6379
            timeout = 2000
            usePool = true
            dbIndex = 0
            convertPipelineAndTxResults = true
        }
        poolConfig {
            maxTotal = 8
            maxIdle = 8
            minIdle = 0
        }
        sentinel {
            master = null
            nodes = []
            password = ''
            timeout = 2000
        }
    }

    mongo {
        hostName = "localhost"
        port = 27017
        database = "spring-session"
        username = ""
        password = ""
        collectionName = MongoOperationsSessionRepository.DEFAULT_COLLECTION_NAME
        replicaSet = [
                [:]
        ]
        jackson.modules = []
    }

    jdbc {
        driverClassName = "org.h2.Driver"
        url = "jdbc:h2:~/test"
        username = ""
        password = ""
        tableName = "SessionData"
        pool {
            maxActive = 10
            maxTotal = 20
            minIdle = 3
            maxWaitMillis = 10000
            defaultAutoCommit = true
            defaultReadOnly = false
            defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED
            validationQuery = "SELECT 1"
        }
    }
}
