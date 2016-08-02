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
        compile('org.springframework.session:spring-session:1.2.1.RELEASE')
        compile('org.springframework.data:spring-data-redis:1.4.1.RELEASE')
        compile('redis.clients:jedis:2.5.2')
    }

    plugins {
        build(":release:3.1.1", ":rest-client-builder:2.1.1") {
            export = false
        }
        compile ':webxml:1.4.1'
    }
}
