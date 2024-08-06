package org.hhplus.ticketing.domain.queue;

import org.hhplus.ticketing.domain.queue.model.Queue;

import java.util.Set;

public interface QueueRepository {

    Long getWaitingPosition(String token);
    void addWaiting(Queue queue);
    void addActive(Queue queue);
    void delActive(String token);
    Set<String> getActivatableTokens(long start, long end);
    boolean isValid(String token);
    void activate(Set<String> waitingTokens);
    Long countActiveTokens();
}
