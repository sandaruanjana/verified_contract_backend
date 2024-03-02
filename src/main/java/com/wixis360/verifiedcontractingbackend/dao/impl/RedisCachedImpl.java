package com.wixis360.verifiedcontractingbackend.dao.impl;

import com.wixis360.verifiedcontractingbackend.dao.RedisCached;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Repository
public class RedisCachedImpl implements RedisCached {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final int expire = 1800;

    @Override
    public Set<String> getKeys(byte[] keys) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<byte[]> setByte = connection.keys(keys);
            if (setByte == null || setByte.size() < 1) {
                return null;
            }
            Set<String> set = new HashSet<>();
            for (byte[] key : setByte) {
                byte[] bs = connection.get(key);
                if (bs != null) {
                    set.add(new String(bs, StandardCharsets.UTF_8));
                }  // return null;

                // set.add(SerializeUtil.unserialize(bs));
            }

            return set;
        });
    }

    @Override
    public Object getCached(final byte[] sessionId) {
        return redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] bs = connection.get(sessionId);
            if (bs != null) {
                return new String(bs, StandardCharsets.UTF_8);
            } else {
                return null;
            }
            // return SerializeUtil.unserialize(bs);
        });

    }

    @Override
    public String updateCached(byte[] key, byte[] session, Long expireSec) {
        return (String) this.redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.set(key, session);
            if (expireSec != null) {
                redisConnection.expire(key, expireSec);
            } else {
                redisConnection.expire(key, expire);
            }
            return new String(key);
        });
    }

    @Override
    public String deleteCached(byte[] key) {
        this.redisTemplate.execute((RedisCallback<String>) redisConnection -> {
            redisConnection.del(key);
            return null;
        });
        return null;
    }
}
