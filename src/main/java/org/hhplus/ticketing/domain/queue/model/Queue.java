package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
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

    public static Queue create() {
        return Queue.builder()
                .token(UUID.randomUUID().toString())
                .score(System.currentTimeMillis())
                .build();
    }

    public static Long getPosition(Long position) {
        if (position == null) throw new CustomException(ErrorCode.INVALID_STATE);
        return position + 1;
    }

    public static String getRemainingWaitTime(Long position) {
        if (position <= 0) throw new CustomException(ErrorCode.INVALID_STATE);

        long peopleAhead = position - 1;

        // (앞에 대기중인 유저의 수 / 한 사이클에서 처리할 수 있는 유저의 수) * 한 사이클의 시간
        long totalSeconds = Math.max((peopleAhead / QueueConstants.MAX_ACTIVE_TOKENS) * 10, 10);

        Duration duration = Duration.ofSeconds(totalSeconds);

        return String.format("%02d분 %02d초", duration.toMinutes(), duration.getSeconds() % 60);
    }
}
