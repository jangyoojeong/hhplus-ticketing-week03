package org.hhplus.ticketing.domain.queue.model.constants;

public class QueueConstants {

    public static final String WAITING_KEY = "waiting";
    public static final String ACTIVE_KEY = "active:";
    public static final long INTERVAL_SECONDS = 10;           // 사이클 간격 10초
    public static final long MAX_ACTIVE_TOKENS = 250;         // 사이클당 250명 활성화
    public static final long TTL_SECONDS = 7 * 60;           // 7분
}