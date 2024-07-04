package org.hhplus.ticketing.presentation.user.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPointResponse {

    private String uuid;                    // uuid
    private int point;                      // 충전 후 포인트 잔액
}
