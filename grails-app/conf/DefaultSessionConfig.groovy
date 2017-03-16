springsession {
    maxInactiveInterval = 1800
    redis {
        connectionFactory {
            hostName = "localhost"
            port = 6379
            password = null
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
    strategy {
        defaultStrategy = "COOKIE"
        cookie.name = "SESSION"
        cookie.path = "/"
        cookie.domainNamePattern = '^.+?\\.(\\w+\\.[a-z]+)$'
        httpHeader.headerName = "x-auth-token"
    }
    isolate.securityContext = false
    allow.persist.mutable = false
}
