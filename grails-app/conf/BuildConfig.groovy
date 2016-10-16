grails.project.work.dir = 'target'

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'

    repositories {
        mavenLocal()
        grailsCentral()
        mavenCentral()
    }

    dependencies {
        compile('org.springframework:spring-core:4.1.1.RELEASE')
        compile('org.springframework.session:spring-session:1.2.1.RELEASE')
        compile('org.springframework.data:spring-data-redis:1.4.1.RELEASE')
        compile('org.springframework.data:spring-data-mongodb:1.8.0.RELEASE')
        compile('com.fasterxml.jackson.core:jackson-databind:2.8.1')
        compile('org.apache.commons:commons-dbcp2:2.0')
        compile('redis.clients:jedis:2.5.2')
        compile('com.hazelcast:hazelcast-client:3.5')

        compile('org.springframework:spring-websocket:4.0.5.RELEASE')
        compile('org.springframework:spring-messaging:4.0.5.RELEASE')

        provided('com.h2database:h2:1.4.191')
    }

    plugins {
        build(":release:3.1.1", ":rest-client-builder:2.1.1") {
            export = false
        }
        compile ':webxml:1.4.1'
    }
}
