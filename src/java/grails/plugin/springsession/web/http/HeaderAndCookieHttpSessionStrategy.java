package grails.plugin.springsession.web.http;

import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.MultiHttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jitendra on 11/6/15.
 */
public class HeaderAndCookieHttpSessionStrategy implements MultiHttpSessionStrategy {

    HeaderHttpSessionStrategy headerHttpSessionStrategy;
    CookieHttpSessionStrategy cookieHttpSessionStrategy;

    public HeaderAndCookieHttpSessionStrategy(HeaderHttpSessionStrategy headerHttpSessionStrategy, CookieHttpSessionStrategy cookieHttpSessionStrategy) {
        this.headerHttpSessionStrategy = headerHttpSessionStrategy;
        this.cookieHttpSessionStrategy = cookieHttpSessionStrategy;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest httpServletRequest) {
        String sessionId = headerHttpSessionStrategy.getRequestedSessionId(httpServletRequest);
        if (isNull(sessionId)) {
            sessionId = cookieHttpSessionStrategy.getRequestedSessionId(httpServletRequest);
        }
        return sessionId;
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        headerHttpSessionStrategy.onNewSession(session, httpServletRequest, httpServletResponse);
        cookieHttpSessionStrategy.onNewSession(session, httpServletRequest, httpServletResponse);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            headerHttpSessionStrategy.onInvalidateSession(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
        }
        try {
            cookieHttpSessionStrategy.onInvalidateSession(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
        }
    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return cookieHttpSessionStrategy.wrapRequest(httpServletRequest, httpServletResponse);
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return cookieHttpSessionStrategy.wrapResponse(httpServletRequest, httpServletResponse);
    }

    private boolean notNull(String testString) {
        return (testString != null && !"".equals(testString));
    }

    private boolean isNull(String testStrung) {
        return !notNull(testStrung);
    }
}
