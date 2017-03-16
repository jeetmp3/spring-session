package grails.plugin.springsession.data.redis;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class RedisSecurityContextRepository implements SecurityContextRepository {

	public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
	public static final String SECURITY_CONTEXT_ID = "securityContextId";

	protected final Log logger = LogFactory.getLog(this.getClass());

	private SecurityContextDao securityContextDao;
	/** SecurityContext instance used to check for equality with default (unauthenticated) content */
	private Object contextObject = SecurityContextHolder.createEmptyContext();
	private boolean allowSessionCreation = true;
	private boolean disableUrlRewriting = false;

	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		HttpServletRequest request = requestResponseHolder.getRequest();
		HttpServletResponse response = requestResponseHolder.getResponse();
		HttpSession httpSession = request.getSession(false);

		String securityContextId = readSecurityContextId(request);
		SecurityContext context = readSecurityContextFromRedis(securityContextId);

		if (context == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No SecurityContext was available. A new one will be created.");
			}
			context = SecurityContextHolder.createEmptyContext();
		}

		requestResponseHolder.setResponse(new SaveToSessionResponseWrapper(response, request,
				httpSession != null, context.hashCode()));

		return context;
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		return readSecurityContextFromRedis(request) != null;
	}

	@Override
	public void saveContext(SecurityContext context, HttpServletRequest request,
							HttpServletResponse response) {
		SaveToSessionResponseWrapper responseWrapper = (SaveToSessionResponseWrapper)response;
		// saveContext() might already be called by the response wrapper
		// if something in the chain called sendError() or sendRedirect(). This ensures we only call it
		// once per request.
		if (!responseWrapper.isContextSaved() ) {
			responseWrapper.saveContext(context);
		}
	}

	private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
		String securityContextId = readSecurityContextId(request);
		return readSecurityContextFromRedis(securityContextId);
	}

	private SecurityContext readSecurityContextFromRedis(String securityContextId) {
		if (StringUtils.isNotEmpty(securityContextId)) {
			return securityContextDao.getSecurityContext(securityContextId);
		}
		return null;
	}

	private String readSecurityContextId(HttpServletRequest request) {
		String securityContextId = request.getParameter(SECURITY_CONTEXT_ID);

		if(StringUtils.isEmpty(securityContextId)){
			HttpSession httpSession = request.getSession(false);

			if(httpSession != null){
				securityContextId = (String) httpSession.getAttribute(SECURITY_CONTEXT_ID);
			}
		}

		return securityContextId;
	}

	public void setSecurityContextDao(SecurityContextDao securityContextDao) {
		this.securityContextDao = securityContextDao;
	}

	public void setAllowSessionCreation(boolean allowSessionCreation) {
		this.allowSessionCreation = allowSessionCreation;
	}

	public void setDisableUrlRewriting(boolean disableUrlRewriting) {
		this.disableUrlRewriting = disableUrlRewriting;
	}

	/**
	 * Wrapper that is applied to every request/response to update the <code>HttpSession<code> with
	 * the <code>SecurityContext</code> when a <code>sendError()</code> or <code>sendRedirect</code>
	 * happens. See SEC-398.
	 * <p>
	 * Stores the necessary state from the start of the request in order to make a decision about whether
	 * the security context has changed before saving it.
	 */
	final class SaveToSessionResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {

		private HttpServletRequest request;

		private boolean httpSessionExistedAtStartOfRequest;
		private int contextHashBeforeChainExecution;
		/**
		 * Takes the parameters required to call <code>saveContext()</code> successfully in
		 * addition to the request and the response object we are wrapping.
		 *
		 * @param request the request object (used to obtain the session, if one exists).
		 * @param httpSessionExistedAtStartOfRequest indicates whether there was a session in place before the
		 *        filter chain executed. If this is true, and the session is found to be null, this indicates that it was
		 *        invalidated during the request and a new session will now be created.
		 * @param contextHashBeforeChainExecution the hashcode of the context before the filter chain executed.
		 *        The context will only be stored if it has a different hashcode, indicating that the context changed
		 *        during the request.
		 */
		SaveToSessionResponseWrapper(HttpServletResponse response, HttpServletRequest request,
									 boolean httpSessionExistedAtStartOfRequest,
									 int contextHashBeforeChainExecution) {
			super(response, disableUrlRewriting);
			this.request = request;
			this.httpSessionExistedAtStartOfRequest = httpSessionExistedAtStartOfRequest;
			this.contextHashBeforeChainExecution = contextHashBeforeChainExecution;
		}

		/**
		 * Stores the supplied security context in the session (if available) and if it has changed since it was
		 * set at the start of the request. If the AuthenticationTrustResolver identifies the current user as
		 * anonymous, then the context will not be stored.
		 *
		 * @param context the context object obtained from the SecurityContextHolder after the request has
		 *        been processed by the filter chain. SecurityContextHolder.getContext() cannot be used to obtain
		 *        the context as it has already been cleared by the time this method is called.
		 *
		 */
		@Override
		protected void saveContext(SecurityContext context) {
			final Authentication authentication = context.getAuthentication();
			HttpSession httpSession = request.getSession(false);
			String securityContextId = readSecurityContextId(request);

			// See SEC-776
			if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
				if (logger.isDebugEnabled()) {
					logger.debug("SecurityContext is empty or anonymous - context will not be stored. ");
				}

				// SEC-1587 A non-anonymous context may still be in the session
				removeSecurityContext(httpSession, securityContextId);
				return;
			}

			if (httpSession == null) {
				httpSession = createNewSessionIfAllowed(context);
			}

			if (httpSession != null) {
				// Update the SecurityContext on Redis and the key on the session only if the SecurityContext
				// was changed or wasn't registered yet
				if((context.hashCode() != contextHashBeforeChainExecution ||
						httpSession.getAttribute(SECURITY_CONTEXT_ID) == null)){
					if(StringUtils.isEmpty(securityContextId)){
						securityContextId = UUID.randomUUID().toString();
					}
					httpSession.setAttribute(SECURITY_CONTEXT_ID, securityContextId);
					securityContextDao.saveSecurityContext(securityContextId, context);


					if (logger.isDebugEnabled()) {
						logger.debug("SecurityContext stored: '" + context + "'");
					}
				}
				// Atualiza o tempo de expiração do SecurityContext no Redis
				long expireTime = httpSession.getLastAccessedTime() + httpSession.getMaxInactiveInterval()*1000;
				securityContextDao.setExpireTime(securityContextId, expireTime);
			}
		}

		private HttpSession createNewSessionIfAllowed(SecurityContext context) {
			if (httpSessionExistedAtStartOfRequest) {
				if (logger.isDebugEnabled()) {
					logger.debug("HttpSession is now null, but was not null at start of request; "
							+ "session was invalidated, so do not create a new session");
				}

				return null;
			}

			if (!allowSessionCreation) {
				if (logger.isDebugEnabled()) {
					logger.debug("The HttpSession is currently null, and the "
							+ HttpSessionSecurityContextRepository.class.getSimpleName()
							+ " is prohibited from creating an HttpSession "
							+ "(because the allowSessionCreation property is false) - SecurityContext thus not "
							+ "stored for next request");
				}

				return null;
			}
			// Generate a HttpSession only if we need to

			if (contextObject.equals(context)) {
				if (logger.isDebugEnabled()) {
					logger.debug("HttpSession is null, but SecurityContext has not changed from default empty context: ' "
							+ context
							+ "'; not creating HttpSession or storing SecurityContext");
				}

				return null;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("HttpSession being created as SecurityContext is non-default");
			}

			try {
				return request.getSession(true);
			} catch (IllegalStateException e) {
				// Response must already be committed, therefore can't create a new session
				logger.warn("Failed to create a session, as response has been committed. Unable to store" +
						" SecurityContext.");
			}

			return null;
		}

	}

	private void removeSecurityContext(HttpSession httpSession, String securityContextId) {
		if (httpSession != null) {
			httpSession.removeAttribute(SECURITY_CONTEXT_ID);
		}
		if(securityContextId != null){
			securityContextDao.deleteSecurityContext(securityContextId);
		}
	}

	void removeSecurityContext(HttpServletRequest request){
		String securityContextId = readSecurityContextId(request);
		removeSecurityContext(request.getSession(false), securityContextId);
	}


}
