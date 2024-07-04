package org.hhplus.ticketing.presentation.reservation.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveSeatResponse {

    private Long reservationId;             // 예약ID
    private String uuid;                    // uuid
    private Long concertOptionId;           // 콘서트옵션ID
    private int seatNumber;                 // 좌석번호

}
