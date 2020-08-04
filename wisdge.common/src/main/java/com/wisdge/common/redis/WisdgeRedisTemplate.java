package com.wisdge.common.redis;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

public class WisdgeRedisTemplate extends org.springframework.data.redis.core.RedisTemplate<String, Object> implements IRedisTemplate {
    private String scope;

    public String getScope() {
        return scope;
    }

    public WisdgeRedisTemplate(String scope) {
        this.scope = scope;
        this.setKeySerializer(new StringRedisSerializer());
        this.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    @Override
    public void set(String key, Object value) {
        this.opsForValue().set(scope + ":" + key, value);
    }

    @Override
    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        this.opsForValue().set(scope + ":" + key, value, ttl, timeUnit);
    }

    @Override
    public Object get(String key) {
        return this.opsForValue().get(scope + ":" + key);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return super.expire(scope + ":" + key, timeout, unit);
    }

    @Override
    public Boolean delete(String key) {
        return super.delete(scope + ":" + key);
    }


}
