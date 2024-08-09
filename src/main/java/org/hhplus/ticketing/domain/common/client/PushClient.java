package org.hhplus.ticketing.domain.common.client;

import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.springframework.stereotype.Component;

@Component
public interface PushClient {
    boolean sendKakaotalk(String msg, PaymentEvent.Success event);
}
