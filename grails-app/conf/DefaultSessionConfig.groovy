import grails.plugin.springsession.enums.Serializer
import grails.plugin.springsession.enums.SessionStore
import grails.plugin.springsession.enums.SessionStrategy
import org.springframework.session.data.mongo.MongoOperationsSessionRepository

import java.sql.Connection

springsession {
    maxInactiveIntervalInSeconds = 1800
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
        jackson.modules = []
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

//    hazelcast {
//        configurationUrl {
//            protocol = "http"
//            host = "localhost"
//            port = 6170
//            file = ""
//        }
//        configurationFile = ""
//        properties = [:]
//        instanceName = "Localhost"
//        group {
//            name = "demo"
//            password = ""
//        }
//        network {
//            port = 5701
//            portCount = 100
//            portAutoIncrement = true
//            reuseAddress = false
//            publicAddress = ""
//            outboundPortDefinitions = []
//            outboundPorts = []
//            interfaces {
//                enabled = false
//                interfaceSet = []
//            }
//            join {
//                multicast {
//                    enabled = false
//                    multicastGroup = "224.2.2.3"
//                    multicastPort = 54327
//                    multicastTimeoutSeconds = 2
//                    multicastTimeToLive = 32
//                    trustedInterfaces = []
//                    loopbackModeEnabled = false
//                }
//                tcpIp {
//                    enabled = false
//                    members = []
//                    requiredMember = ""
//                }
//                aws {
//                    enabled = false
//                    accessKey = ""
//                    secretKey = ""
//                    region = "us-east-1"
//                    securityGroupName = ""
//                    tagKey = ""
//                    tagValue = ""
//                    hostHeader = "ec2.amazonaws.com"
//                    connectionTimeoutSeconds = 5
//                }
//            }
//            symmetricEncryption {
//                enabled = false
//                salt = "thesalt"
//                password = "thepassword"
//                iterationCount = 19
//                algorithm = "PBEWithMD5AndDES"
//                key = "key"
//            }
//            socketInterceptor {
//                enabled = false
//                className = ""
//                implementation = null
//                properties = [:]
//            }
//            ssl {
//                enabled = false
//                factoryClassName = ""
//                factoryImplementation = null
//                properties = [:]
//            }
//        }
//
//    }
}
