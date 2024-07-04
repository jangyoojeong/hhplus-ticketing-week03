package org.hhplus.ticketing.presentation.consert.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatsForReservationResponse {

    private Long concertOptionId;
    private int[] availableSeats;

}
