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
        compile('org.springframework.session:spring-session-data-redis:1.0.1.RELEASE') {
            excludes("spring-context", "spring-context-support", "spring-aop")
        }
    }

    plugins {
        build(":release:3.1.1", ":rest-client-builder:2.1.1") {
            export = false
        }
    }
}
