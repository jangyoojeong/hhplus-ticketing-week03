package org.hhplus.ticketing.presentation.reservation.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveSeatRequest {

    private String uuid;                    // uuid
    private Long concertOptionId;           // 콘서트옵션ID
    private int seatNumber;                 // 좌석번호

}
