package grails.plugin.springsession

import org.codehaus.groovy.grails.web.util.WebUtils

import javax.servlet.http.HttpSession

class SessionProxy {

	HttpSession getSession() {
		WebUtils.retrieveGrailsWebRequest().session
	}

}
