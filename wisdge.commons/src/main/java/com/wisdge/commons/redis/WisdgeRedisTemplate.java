package com.wisdge.commons.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.concurrent.TimeUnit;

public class WisdgeRedisTemplate extends org.springframework.data.redis.core.RedisTemplate<String, Object> implements IRedisTemplate {
    private String scope;

    public String getScope() {
        return scope;
    }

    public WisdgeRedisTemplate(String scope) {
        this.scope = scope;

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        this.setKeySerializer(stringRedisSerializer);
        this.setHashKeySerializer(stringRedisSerializer);

        // 需要支持范型时使用 GenericJackson2JsonRedisSerializer，但是会有更多的系统开销
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//        this.setValueSerializer(genericJackson2JsonRedisSerializer);
//        this.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        this.setValueSerializer(jackson2JsonRedisSerializer);
        this.setHashValueSerializer(jackson2JsonRedisSerializer);

        this.afterPropertiesSet();
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
    public Object entries(String key) {
        return this.opsForHash().entries(scope + ":" + key);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return super.expire(scope + ":" + key, timeout, unit);
    }

    @Override
    public Boolean delete(String key) {
        return super.delete(scope + ":" + key);
    }

    @Override
    public void convertAndSend(String channel, Object obj) {
        super.convertAndSend(channel, obj);
    }

    @Override
    public RedisConnectionFactory getConnectionFactory() {
        return super.getConnectionFactory();
    }
}
