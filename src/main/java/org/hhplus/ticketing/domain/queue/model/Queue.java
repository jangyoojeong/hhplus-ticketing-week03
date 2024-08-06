package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;

import java.time.Duration;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {

    private String token;                          // 발급된 토큰
    private long score;                            // 토큰 발급 시각
    private Long position;                         // 대기순서
    private String remainingTime;                  // 잔여시간

    public static Queue create() {
        return Queue.builder()
                .token(UUID.randomUUID().toString())
                .score(System.currentTimeMillis())
                .build();
    }

    public static Queue getWaitingInfo(Long position) {
        return Queue.builder()
                .position(position)
                .remainingTime(Queue.getRemainingWaitTime(position))
                .build();
    }

    public static Long getPosition(Long position) {
        return position == null ? 0 : position + 1;
    }

    public static String getRemainingWaitTime(Long position) {
        if (position <= 0) return null;

        long peopleAhead = position - 1;

        // (앞에 대기중인 유저의 수 / 한 사이클에서 처리할 수 있는 유저의 수) * 한 사이클의 시간
        long totalSeconds = (long) Math.ceil((double) peopleAhead / QueueConstants.MAX_ACTIVE_TOKENS) * QueueConstants.INTERVAL_SECONDS;

        // 최소 한 사이클의 시간 추가
        totalSeconds = Math.max(totalSeconds, QueueConstants.INTERVAL_SECONDS);

        Duration duration = Duration.ofSeconds(totalSeconds);
        return String.format("%02d시간 %02d분 %02d초", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
    }
}
