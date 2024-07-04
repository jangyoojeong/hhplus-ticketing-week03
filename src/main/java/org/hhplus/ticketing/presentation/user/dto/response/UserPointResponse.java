package org.hhplus.ticketing.presentation.user.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPointResponse {

    private String uuid;                    // uuid
    private int point;                      // 포인트 잔액
}
