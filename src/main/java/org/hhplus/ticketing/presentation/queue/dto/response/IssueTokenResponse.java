package org.hhplus.ticketing.presentation.queue.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueTokenResponse {

    private String uuid;                    // uuid
    private String token;                   // 토큰
    private int position;                   // 대기순서

}
