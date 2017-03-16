package grails.plugin.springsession.data.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;

import java.util.concurrent.TimeUnit;

public class SecurityContextDao {

	private RedisTemplate redisTemplate;

	public void saveSecurityContext(String securityContextId, SecurityContext context) {
		redisTemplate.opsForValue().set(formatKey(securityContextId), context);
	}

	public void setExpireTime(String securityContextId, long expireTime){
		redisTemplate.expire(formatKey(securityContextId), expireTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public void deleteSecurityContext(String securityContextId) {
		redisTemplate.delete(formatKey(securityContextId));
	}

	public SecurityContext getSecurityContext(String securityContextId) {
		return (SecurityContext) redisTemplate.opsForValue().get(formatKey(securityContextId));
	}

	public String formatKey(String securityContextId) {
		return String.format("%s:%s", RedisSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContextId);
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
