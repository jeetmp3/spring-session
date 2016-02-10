package spring.session.demo.app

import grails.test.mixin.integration.Integration
import grails.transaction.*

import spock.lang.*
import geb.spock.*

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
class SessionPluginSpec extends GebSpec {

    def grailsApplication

    def setup() {
    }

    def cleanup() {
    }

    void "test set name"() {
        when: 'Setting key name in session with value. And in another call we are getting the same key from session'
            go '/springSessionDemo/set?key=name&value=Jitendra'
            go '/springSessionDemo/get?key=name'

        then:"Value should be same as we passed in name parameter"
        	$().text() == "Jitendra"
    }

    void "test Mutable objects"() {
        when: 'Setting map value and in next call we are updating the map value finally we are getting the same value. (by default update mutable is false)'
        go '/springSessionDemo/setMutable?name=Value1'
        go '/springSessionDemo/updateMutable?name=Value2'
        go '/springSessionDemo/getFinalValue?name=name'

        then: "Map should not be updated in session. value should not be the last updated value"
        $().text() != "Value2"
    }

    void "test Mutable objects after enabling mutable true" () {
        setup: "Setting Persist mutable property true"
        ConfigObject configObject = new ConfigObject()
        configObject.allow.persist.mutable = true
        setMutableUpdateTrue(configObject)

        when: "Setting and then updating the map value. Finally we are accessing the same value"
        go '/springSessionDemo/setMutable?name=Value1'
        go '/springSessionDemo/updateMutable?name=Value2'
        go '/springSessionDemo/getFinalValue?name=name'

        then: " Value must be the updated one"
        $().text() == "Value1"
    }

    void "test session timeout" () {
        setup: "Setting default session timeout to 5 seconds"
        ConfigObject configObject = new ConfigObject()
        configObject.maxInactiveInterval = 2
        setMutableUpdateTrue(configObject)

        when: 'setting the key first and then accessing it after 6 seconds'
        go '/springSessionDemo/set?key=newName&value=Jitendra'
        sleep(1000 * 60 * 2)
        go '/springSessionDemo/get?key=newName'

        then: "Key [name] should be expired"
        $().text() == "NO-KEY"
    }

    def setMutableUpdateTrue(ConfigObject configObject) {
        grailsApplication.config.springsession = configObject
    }
}
