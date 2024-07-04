package org.hhplus.ticketing.presentation.payment.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;                 // 결제ID
    private String uuid;                    // uuid
    private int point;                      // 포인트 (결제 후 포인트)

}
