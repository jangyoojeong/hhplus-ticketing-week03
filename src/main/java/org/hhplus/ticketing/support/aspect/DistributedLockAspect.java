package org.hhplus.ticketing.support.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hhplus.ticketing.support.annotation.DistributedLock;
import org.hhplus.ticketing.support.annotation.DistributedPubSubLock;
import org.hhplus.ticketing.support.annotation.DistributedSpinLock;
import org.hhplus.ticketing.support.util.LockKeyParser;
import org.redisson.api.RLock;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @DistributedLock 및 @DistributedPubSubLock, @DistributedSpinLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@Slf4j
public class DistributedLockAspect {

    @Autowired
    private RedissonClient redissonClient;

    private static final int SPIN_LOCK_RETRY_DELAY = 100; // SpinLock 재시도 간격 (밀리초)
    private static final int SPIN_LOCK_EXPIRE = 5;  // SpinLock 만료 시간 (초)
    private static final String PUBSUB_TOPIC = "lockTopic"; // Pub/Sub 토픽 이름

    @Around("@annotation(distributedLock)")
    public Object handleDistributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        return handleLock(joinPoint, distributedLock.key(), distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit(), LockType.DEFAULT);
    }

    @Around("@annotation(distributedSpinLock)")
    public Object handleDistributedSpinLock(ProceedingJoinPoint joinPoint, DistributedSpinLock distributedSpinLock) throws Throwable {
        return handleLock(joinPoint, distributedSpinLock.key(), SPIN_LOCK_RETRY_DELAY, SPIN_LOCK_EXPIRE, TimeUnit.SECONDS, LockType.SPIN);
    }

    @Around("@annotation(distributedPubSubLock)")
    public Object handleDistributedPubSubLock(ProceedingJoinPoint joinPoint, DistributedPubSubLock distributedPubSubLock) throws Throwable {
        return handleLock(joinPoint, distributedPubSubLock.key(), distributedPubSubLock.waitTime(), distributedPubSubLock.leaseTime(), distributedPubSubLock.timeUnit(), LockType.PUBSUB);
    }

    private Object handleLock(ProceedingJoinPoint joinPoint, String keyExpression, long waitTime, long leaseTime, TimeUnit timeUnit, LockType lockType) throws Throwable {
        String key = LockKeyParser.parseKey(joinPoint, keyExpression);
        if (key == null) {
            throw new IllegalArgumentException("분산 락 키는 null이 될 수 없습니다.");
        }
        RLock lock = redissonClient.getLock(key);

        try {
            log.info("키 {}로 락 획득 시도 중", key);
            boolean isLocked = false;

            if (lockType == LockType.SPIN) {
                // Spin lock 로직
                while (!(isLocked = lock.tryLock(waitTime, leaseTime, timeUnit))) {
                    log.info("락 획득 실패, 재시도 중...");
                    Thread.sleep(waitTime);
                }
            } else {
                // Default 및 PubSub 락 로직
                isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            }

            if (!isLocked) {
                log.warn("키 {}로 락을 획득할 수 없습니다.", key);
                throw new RuntimeException("락을 획득할 수 없습니다.");
            }

            log.info("키 {}로 락을 성공적으로 획득했습니다.", key);

            // Pub/Sub 락 상태 알림
            if (lockType == LockType.PUBSUB) {
                notifyLockAcquired(key);
            }

            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("락이 걸린 메서드 실행 중 예외 발생");
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("키 {}로 락을 해제했습니다.", key);

                // Pub/Sub 락 해제 알림
                if (lockType == LockType.PUBSUB) {
                    notifyLockReleased(key);
                }
            } else {
                log.warn("키 {}로 락이 현재 스레드에 의해 유지되지 않음, unlock 생략", key);
            }
        }
    }

    /**
     * 락 획득을 Pub/Sub으로 알림.
     *
     * @param key 락 키
     */
    private void notifyLockAcquired(String key) {
        RTopic topic = redissonClient.getTopic(PUBSUB_TOPIC);
        topic.publish("락 획득: " + key);
    }

    /**
     * 락 해제를 Pub/Sub으로 알림.
     *
     * @param key 락 키
     */
    private void notifyLockReleased(String key) {
        RTopic topic = redissonClient.getTopic(PUBSUB_TOPIC);
        topic.publish("락 해제: " + key);
    }

    private enum LockType {
        DEFAULT,
        PUBSUB,
        SPIN
    }
}