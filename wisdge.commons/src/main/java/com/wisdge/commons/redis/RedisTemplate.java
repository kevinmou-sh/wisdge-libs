package com.wisdge.commons.redis;

import com.wisdge.utils.StringUtils;
import lombok.Data;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
public class RedisTemplate extends org.springframework.data.redis.core.RedisTemplate<String, Object> implements IRedisTemplate {
    private String scope;
    private String app;

    public RedisTemplate(String scope) {
        this(scope, null);
    }

    public RedisTemplate(String scope, String app) {
        this.scope = scope;
        this.app = app;

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        this.setKeySerializer(stringRedisSerializer);
        this.setHashKeySerializer(stringRedisSerializer);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        this.setValueSerializer(jackson2JsonRedisSerializer);
        this.setHashValueSerializer(jackson2JsonRedisSerializer);
    }

    public String getKey(String key) {
        String prefix = StringUtils.isNotEmpty(scope) ? (scope + ":") : "";
        if (StringUtils.isNotEmpty(app)) prefix += app + ":";
        return prefix + key;
    }

    public String getGlobalKey(String key) {
        String prefix = StringUtils.isNotEmpty(scope) ? (scope + ":") : "";
        return prefix + key;
    }

    @Override
    public void set(String key, Object value) {
        super.opsForValue().set(getKey(key), value);
    }

    public void setGlobal(String key, Object value) {
        super.opsForValue().set(getGlobalKey(key), value);
    }

    @Override
    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        super.opsForValue().set(getKey(key), value, ttl, timeUnit);
    }

    @Override
    public void setGlobal(String key, Object value, long ttl, TimeUnit timeUnit) {
        super.opsForValue().set(getGlobalKey(key), value, ttl, timeUnit);
    }

    @Override
    public void putHash(String key, String hashKey, Object hashValue) {
        super.opsForHash().put(getKey(key), hashKey, hashValue);
    }

    @Override
    public void putHashGlobal(String key, String hashKey, Object hashValue) {
        super.opsForHash().put(getGlobalKey(key), hashKey, hashValue);
    }

    @Override
    public Object get(String key) {
        return super.opsForValue().get(getKey(key));
    }

    public Object getGlobal(String key) {
        return super.opsForValue().get(getGlobalKey(key));
    }

    @Override
    public Map<Object, Object> entries(String key) {
        return super.opsForHash().entries(getKey(key));
    }

    public Map<Object, Object> entriesGlobal(String key) {
        return super.opsForHash().entries(getGlobalKey(key));
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return super.expire(getKey(key), timeout, unit);
    }

    public Boolean expireGlobal(String key, long timeout, TimeUnit unit) {
        return super.expire(getGlobalKey(key), timeout, unit);
    }

    @Override
    public Boolean delete(String key) {
        return super.delete(getKey(key));
    }

    public Boolean deleteGlobal(String key) {
        return super.delete(getGlobalKey(key));
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
