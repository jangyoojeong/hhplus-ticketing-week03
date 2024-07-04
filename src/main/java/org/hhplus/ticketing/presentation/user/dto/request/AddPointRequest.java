package org.hhplus.ticketing.presentation.user.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPointRequest {

    private String uuid;                    // uuid
    private int amount;                     // 충전금액
}
