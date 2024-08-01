package org.hhplus.ticketing.domain.queue.model.constants;

public class QueueConstants {

    public static final String WAITING_KEY = "waiting";
    public static final String ACTIVE_KEY = "active:";
    public static final long MAX_ACTIVE_TOKENS = 250;        // 사이클당 100명 활성화
    public static final long TTL_SECONDS = 7 * 60;           // 7분

}