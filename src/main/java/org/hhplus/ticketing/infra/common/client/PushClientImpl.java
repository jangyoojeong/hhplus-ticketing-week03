package org.hhplus.ticketing.infra.common.client;

import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.client.PushClient;
import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushClientImpl implements PushClient {

    @Override
    public boolean sendKakaotalk(String msg, PaymentEvent.Success event) {
        try {
            Thread.sleep(5000L);                                  // 외부 API작업이 5초 걸린다고 가정
            log.info("카카오톡 전송 성공: {}", msg);
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException("카카오톡 전송 클라이언트에서 예외 반환", e);
        }
    }
}
