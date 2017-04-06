Map<String, String> ENV = System.getenv();
String mvnRepoHostDeploy = ENV['MVN_REPO_HOST']
String mvnRepoUserDeploy = ENV['MVN_REPO_USER']
String mvnRepoPasswordDeploy = ENV['MVN_REPO_PASSWORD']

grails.project.work.dir = 'target'

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
	inherits 'global'
	log 'warn'

	repositories {
		mavenLocal()
		grailsCentral()
		mavenCentral()
		mavenRepo ENV['MVN_REPO_REPOSITORIES_SONATYPE']
		mavenRepo ENV['MVN_REPO_REPOSITORIES_URL_LIBS']
		mavenRepo ENV['MVN_REPO_REPOSITORIES_GRAILS_PLUGINS']
	}

	credentials {
		realm = ENV['MVN_REPO_REALM']
		host = mvnRepoHostDeploy
		username = mvnRepoUserDeploy
		password = mvnRepoPasswordDeploy
	}

	dependencies {
		compile('org.grails.plugins:spring-session-base:1.0.1-SNAPSHOT') {
			excludes(
					[group: "org.springframework", name: "spring-core"],
					[group: "org.springframework", name: "spring-context"],
					[group: "org.springframework", name: "spring-context-support"],
					[group: "org.springframework", name: "spring-aop"],
					[group: "org.springframework", name: "spring-tx"],
					[group: "org.springframework", name: "spring-expression"],
					[group: "org.springframework", name: "spring-web"],
					[group: "javax.servlet", name: "javax.servlet-api"]
			)
		}

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

grails.project.repos.releases.url = ENV['MVN_REPO_REPOSITORIES_URL_PLUGINS_RELEASE']
grails.project.repos.releases.username = mvnRepoUserDeploy
grails.project.repos.releases.password = mvnRepoPasswordDeploy

grails.project.repos.snapshots.url = ENV['MVN_REPO_REPOSITORIES_URL_PLUGINS_SNAPSHOT']
grails.project.repos.snapshots.username = mvnRepoUserDeploy
grails.project.repos.snapshots.password = mvnRepoPasswordDeploy

grails.project.repos.default = 'snapshots'
