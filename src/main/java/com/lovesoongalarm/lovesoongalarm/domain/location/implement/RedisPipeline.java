package com.lovesoongalarm.lovesoongalarm.domain.location.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisPipeline {
    private final StringRedisTemplate stringRedisTemplate;

    public List<Object> pipe(java.util.function.Consumer<RedisOperations<String, String>> block) {
        SessionCallback<Object> cb = new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) throws DataAccessException {
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
                block.accept(ops);
                return null;
            }
        };
        return stringRedisTemplate.executePipelined(cb);
    }
}
