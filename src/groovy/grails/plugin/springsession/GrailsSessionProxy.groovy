package grails.plugin.springsession

import org.codehaus.groovy.grails.web.util.WebUtils

import javax.servlet.http.HttpSession

class GrailsSessionProxy implements SessionProxy {

	@Override
	HttpSession getSession() {
		WebUtils.retrieveGrailsWebRequest().session
	}

}
