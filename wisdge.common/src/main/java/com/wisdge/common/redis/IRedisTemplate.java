package com.wisdge.common.redis;

import java.util.concurrent.TimeUnit;

public interface IRedisTemplate {
	public void set(String key, Object value);
	public void set(String key, Object value, long ttl, TimeUnit timeUnit);
	public Object get(String key);
	public Boolean expire(String key, long timeout, TimeUnit unit);
	public Boolean delete(String key);
}
