package grails.plugin.springsession.data.redis;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedisLogoutHandler implements LogoutHandler{

	private RedisSecurityContextRepository redisSecurityContextRepository;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		redisSecurityContextRepository.removeSecurityContext(request);
	}

	public void setRedisSecurityContextRepository(RedisSecurityContextRepository redisSecurityContextRepository) {
		this.redisSecurityContextRepository = redisSecurityContextRepository;
	}
}
