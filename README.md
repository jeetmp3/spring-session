# spring-session
SpringSession plugin provides [spring-session](http://projects.spring.io/spring-session) support in grails application. SpringSession uses Redis to persist the HTTP Sessions.
You can find the spring session documentation here: http://docs.spring.io/spring-session/docs/1.0.1.RELEASE/reference/html5/

## Using
Add a plugin in BuildConfig.groovy
```
plugins {
    runtime ":spring-session:0.1"
    ...
}
```
This will add a servlet filter in your application to handle HTTP session baked by SpringSession. By default it uses host `localhost` and port `6397` for Redis. Default session strategy is `Cookie` based. You can override it to `HTTP Header` based.

## Configuration
#### 1. Redis Configuration
To override default Redis configuration add below code in Config.groovy
```
springsession.redis.connectionFactory.hostName = "<redis server ip>"
springsession.redis.connectionFactory.port = 6379
```
#### 2. Session Strategy Configuration
Default session strategy is `Cookie` based and session cookie name is `SESSION` You can override the default session strategy
```
springsession.strategy.defaultStrategy='HEADER'
```
This will enable HTTP Header based session strategy. Default token name is `x-auth-token` you can override this
```
springsession.strategy.token.headerName = "new token name"
```
