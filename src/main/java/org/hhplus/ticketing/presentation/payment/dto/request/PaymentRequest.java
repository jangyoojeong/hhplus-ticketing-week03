package org.hhplus.ticketing.presentation.payment.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private String uuid;                    // uuid
    int price;                              // 결제금액

}
