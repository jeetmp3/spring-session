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
        compile('org.springframework.session:spring-session-data-redis:1.1.1.RELEASE') {
            excludes([group: "org.springframework", name: "spring-context"],
                    [group: "org.springframework", name: "spring-context-support"],
                    [group: "org.springframework", name: "spring-aop"],
                    [group: "org.springframework", name: "spring-core"],
                    [group: "org.springframework", name: "spring-tx"])
        }

        compile('org.springframework.security:spring-security-core:3.0.7.RELEASE') {
            excludes([group: "org.springframework", name: "spring-context"],
                    [group: "org.springframework", name: "spring-context-support"],
                    [group: "org.springframework", name: "spring-aop"],
                    [group: "org.springframework", name: "spring-core"],
                    [group: "org.springframework", name: "spring-tx"],
                    [group: "org.springframework", name: "spring-expression"])
        }
        compile ('org.springframework.security:spring-security-web:3.0.7.RELEASE') {
            excludes([group: "org.springframework", name: "spring-web"],
                    [group: "org.springframework", name: "spring-core"])
        }


//        compile('com.fasterxml.jackson.core:jackson-core:2.6.4')
//        compile('com.fasterxml.jackson.core:jackson-databind:2.6.4')

        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        build ':release:2.2.1', ':rest-client-builder:1.0.3', {
            export = false
        }

        compile ':webxml:1.4.1'

        test(':spock:0.7') {
            exclude 'spock-grails-support'
        }
    }
}
