package grails.plugin.springsession.web;

import org.springframework.session.Session;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.MultiHttpSessionStrategy;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jitendra on 10/6/15.
 */
public class MultiHttpSessionStrategyAdapter implements MultiHttpSessionStrategy {

    HttpSessionStrategy httpSessionStrategy;
    SessionRepositoryFilter sessionRepositoryFilter;

    public MultiHttpSessionStrategyAdapter(HttpSessionStrategy httpSessionStrategy, SessionRepositoryFilter sessionRepositoryFilter) {
        Assert.notNull(httpSessionStrategy, "HttpSessionStrategy must not be null");
        Assert.notNull(sessionRepositoryFilter, "HttpSessionStrategy must not be null");
        this.httpSessionStrategy = httpSessionStrategy;
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public String getRequestedSessionId(HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public void onInvalidateSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }
}
