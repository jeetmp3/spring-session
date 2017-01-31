<h4><a href='https://goo.gl/VmI8yQ' target='_blank'>Grails 2.x Documentation</a></h4>

# spring-session-grails-plugin
This plugin provides [spring-session](http://projects.spring.io/spring-session) support in grails application.  SpringSession provides nice features:
* HttpSession
  * Clustered Sessions
  * Multiple Browser Sessions
  * RESTful APIs
* WebSocket

SpringSession uses Redis to persist the HTTP Sessions.
You can find official documentation for Spring Session project here: http://docs.spring.io/spring-session/docs/1.0.1.RELEASE/reference/html5/

Currently this plugin provides support for HttpSession only. WebSocket support will be added in further release.

## Using

Just add a plugin in BuildConfig.groovy.
```
plugins {
    runtime ":spring-session:1.2.1"
    ...
}
```
Note: Redis must be installed on your machine.

## Configuration
#### 1. Redis Configuration
Default configuration will lookup Redis server on your `localhost` port `6379`. To override default configuration add below code in Config.groovy
```
springsession.redis.connectionFactory.hostName = "<redis server ip>"
springsession.redis.connectionFactory.port = 6379
springsession.redis.connectionFactory.password = "<password>"
springsession.redis.connectionFactory.timeout = 2000
springsession.redis.connectionFactory.usePool = true
springsession.redis.connectionFactory.dbIndex = 0
```

#### 2. Change Session Strategy Configuration
Default session strategy is `Cookie` based and session cookie name is `SESSION` You can override the default session strategy
```
springsession.strategy.defaultStrategy='HEADER'
```

This will enable HTTP Header based session strategy. Default token name is `x-auth-token` you can override this
```
springsession.strategy.token.headerName = "new token name"
```

To change default cookie name add below configuration. 
```
springsession.strategy.cookie.name="Your Cookie name"
```
(**Note: Property ~~springsession.strategy.defaultStrategy.cookie.name~~ is no longer available in version 1.1 and higher**) 

#### 3. Configure redis sentinel
```
springsession.redis.sentinel.master="<Sentinel master name>"
springsession.redis.sentinel.nodes=[[host: "hostname", port: xxxx], [host: "another host", port: xxxx]]
springsession.redis.sentinel.password="Sentinel password"
```

#### 4. Enable update mutable objects in session
By default spring session doesn't update mutable object value stored in session. You can override this behaviour by setting 
below property to `true`. By default this value is `false`.
 ```
 springsession.allow.persist.mutable = true
 ```
## Version Support
* Grails 2.4 +
* Redis 2.8 +
