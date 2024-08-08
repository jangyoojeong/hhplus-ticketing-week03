package org.hhplus.ticketing.infra.common.client;

import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.client.DataPlatformClient;
import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformClientImpl implements DataPlatformClient {

    @Override
    public boolean send(String msg, PaymentEvent.Success event) {
        try {
            Thread.sleep(5000L);                                  // 외부 API작업이 5초 걸린다고 가정
            log.info("데이터 플랫폼 전송 성공: {}", msg);
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException("데이터 플랫폼 전송 클라이언트에서 예외 반환", e);
        }
    }
}
