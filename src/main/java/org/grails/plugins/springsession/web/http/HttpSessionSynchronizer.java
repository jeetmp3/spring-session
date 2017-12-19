package org.grails.plugins.springsession.web.http;

import org.springframework.core.annotation.Order;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author jitendra
 */
@Order(SessionRepositoryFilter.DEFAULT_ORDER + 1)
public class HttpSessionSynchronizer extends OncePerRequestFilter {

    private Boolean persistMutable;


    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(persistMutable);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        if (persistMutable && request != null && request.getSession(false) != null) {
            HttpSession session = request.getSession();
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                try {
                    Object object = session.getAttribute(key);
                    session.setAttribute(key, object);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void setPersistMutable(Boolean persistMutable) {
        this.persistMutable = persistMutable;
    }
}
