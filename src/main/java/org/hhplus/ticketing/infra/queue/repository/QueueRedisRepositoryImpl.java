package org.hhplus.ticketing.infra.queue.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepositoryImpl implements QueueRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;

    @Override
    public Long getWaitingPosition(String token) {
        return zSetOps.rank(QueueConstants.WAITING_KEY, token);
    }

    @Override
    public void addWaiting(Queue queue) {
        zSetOps.add(QueueConstants.WAITING_KEY, queue.getToken(), queue.getScore());
    }

    @Override
    public void addActive(Queue queue) {
        String key = QueueConstants.ACTIVE_KEY + queue.getToken();
        redisTemplate.opsForValue().set(key, queue.getToken());
        redisTemplate.expire(key, QueueConstants.TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void delActive(String token) {
        String key = QueueConstants.ACTIVE_KEY + token;
        redisTemplate.delete(key);
    }

    @Override
    public Set<String> getActivatableTokens(long start, long end) {
        return zSetOps.range(QueueConstants.WAITING_KEY, start, end);
    }

    @Override
    public boolean isValid(String token) {
        String key = QueueConstants.ACTIVE_KEY + token;
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    @Override
    public void activate(Set<String> waitingTokens) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            waitingTokens.forEach(token -> {
                String key = QueueConstants.ACTIVE_KEY + token;

                // 활성 토큰으로 추가하고 TTL 설정
                stringRedisConn.set(key, token);
                stringRedisConn.expire(key, QueueConstants.TTL_SECONDS);

                // 대기열에서 제거
                stringRedisConn.zRem(QueueConstants.WAITING_KEY, token);
            });
            return null;
        });
    }

    @Override
    public Long countActiveTokens() {
        return Optional.ofNullable(redisTemplate.keys(QueueConstants.ACTIVE_KEY + "*"))
                .map(keys -> (long) keys.size())
                .orElse(0L);
    }
}
