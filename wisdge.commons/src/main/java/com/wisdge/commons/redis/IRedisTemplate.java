package com.wisdge.commons.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.concurrent.TimeUnit;

public interface IRedisTemplate {
	public void set(String key, Object value);
	public void set(String key, Object value, long ttl, TimeUnit timeUnit);
	public Object get(String key);
	public Object entries(String key);
	public Boolean expire(String key, long timeout, TimeUnit unit);
	public Boolean delete(String key);
	public void convertAndSend(String channel, Object obj);
	public RedisConnectionFactory getConnectionFactory();
}
