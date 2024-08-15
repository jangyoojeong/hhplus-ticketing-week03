package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.outbox.OutboxRepository;
import org.hhplus.ticketing.domain.outbox.OutboxService;
import org.hhplus.ticketing.domain.outbox.model.Outbox;
import org.hhplus.ticketing.domain.outbox.model.OutboxCommand;
import org.hhplus.ticketing.domain.outbox.model.constants.OutboxConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OutboxIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OutboxRepository outboxRepository;

    @Test
    @DisplayName("🟢 아웃박스_저장_테스트_데이터_저장_후_저장된_정보가_리턴된다")
    public void saveTest_아웃박스_저장_통합_테스트_데이터_저장_후_저장된_정보가_리턴된다() {
        // Given
        String messageKey = "1";
        String domainType = "PAYMENT";
        String eventType = "PAYMENT_SUCCESS";
        String message = "MESSAGE";
        boolean isSent = false;

        Outbox outbox = Outbox.builder()
                .outboxId(1L)
                .messageKey(messageKey)
                .domainType(domainType)
                .eventType(eventType)
                .message(message)
                .isSent(isSent)
                .sentAt(null)
                .build();

        OutboxCommand.save command = new OutboxCommand.save(messageKey, domainType, eventType, message);

        // When
        Outbox result = outboxService.save(command);

        // Then
        assertNotNull(result.getOutboxId());
        assertEquals(outbox.getMessageKey(), result.getMessageKey());
        assertEquals(outbox.getDomainType(), result.getDomainType());
        assertEquals(outbox.getEventType(), result.getEventType());
        assertEquals(outbox.getMessage(), result.getMessage());
        assertEquals(outbox.isSent(), result.isSent());
        assertNull(outbox.getSentAt());
    }
    
    @Test
    @DisplayName("🟢 아웃박스_상태변경_테스트_아웃박스_상태가_발송상태로_변경된다")
    public void updateSentTest_아웃박스_상태변경_통합_테스트_아웃박스_상태가_발송상태로_변경된다() {
        // Given
        String messageKey = "1";
        String domainType = "PAYMENT";
        String eventType = "PAYMENT_SUCCESS";
        String message = "MESSAGE";

        OutboxCommand.save command = new OutboxCommand.save(messageKey, domainType, eventType, message);
        outboxService.save(command);

        OutboxCommand.updateSent updateCommand = new OutboxCommand.updateSent(messageKey, domainType, eventType);

        // When
        Outbox updatedOutbox = outboxService.updateSent(updateCommand);

        // Then
        assertTrue(updatedOutbox.isSent());
        assertNotNull(updatedOutbox.getSentAt());
    }

    @Test
    @DisplayName("🔴 updateSentTest_아웃박스_상태변경_통합_테스트_이미_발행된_상태일_경우_INVALID_STATE_예외반환")
    public void updateSentTest_아웃박스_상태변경_통합_테스트_이미_발행된_상태일_경우_INVALID_STATE_예외반환() {
        // Given
        String messageKey = "1";
        String domainType = "PAYMENT";
        String eventType = "PAYMENT_SUCCESS";
        String message = "MESSAGE";

        OutboxCommand.save command = new OutboxCommand.save(messageKey, domainType, eventType, message);
        outboxService.save(command);

        OutboxCommand.updateSent updateCommand = new OutboxCommand.updateSent(messageKey, domainType, eventType);

        // When
        outboxService.updateSent(updateCommand);

        // Then
        assertThatThrownBy(() -> outboxService.updateSent(updateCommand))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_STATE);
    }

    @Test
    @DisplayName("🔴 updateSentTest_아웃박스_상태변경_테스트_조회된_아웃박스_데이터가_없을경우_OUTBOX_NOT_FOUND_예외반환")
    public void updateSentTest_아웃박스_상태변경_테스트_조회된_아웃박스_데이터가_없을경우_OUTBOX_NOT_FOUND_예외반환() {
        // Given
        String messageKey = "1";
        String domainType = "PAYMENT";
        String eventType = "PAYMENT_SUCCESS";

        OutboxCommand.updateSent updateCommand = new OutboxCommand.updateSent(messageKey, domainType, eventType);

        // Then
        assertThatThrownBy(() -> outboxService.updateSent(updateCommand))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.OUTBOX_NOT_FOUND);
    }

    @Test
    @DisplayName("🟢 메시지_재발행_스케줄러_테스트_대상_리스트_2건_중_1건만_상태가_발송상태로_변경된다")
    public void retryFailedMessagesTest_메시지_재발행_스케줄러_테스트_대상_리스트_2건_중_1건만_상태가_발송상태로_변경된다() {
        // Given
        LocalDateTime retryTargetTime = LocalDateTime.now().minusMinutes(OutboxConstants.OUTBOX_RETRY_THRESHOLD_MINUTES + 1);

        outboxRepository.save(Outbox.builder()
                .messageKey("1")
                .domainType("PAYMENT")
                .eventType("PAYMENT_SUCCESS")
                .message("{\"token\":\"testToken\",\"userId\":1,\"reservationId\":1001,\"price\":150}")
                .isSent(false)
                .createdAt(retryTargetTime)
                .build());

        outboxRepository.save(Outbox.builder()
                .messageKey("2")
                .domainType("PAYMENT")
                .eventType("PAYMENT_SUCCESS")
                .message("{\"token\":\"testToken\",\"userId\":1,\"reservationId\":1001,\"price\":150}")
                .isSent(false)
                .createdAt(LocalDateTime.now())
                .build());

        // When
        outboxService.retryFailedMessages();

        // Then
        Outbox outbox1 = outboxRepository.getOutbox("1", "PAYMENT", "PAYMENT_SUCCESS").orElse(null);
        assertTrue(outbox1.isSent());
        assertNotNull(outbox1.getSentAt());

        Outbox outbox2 = outboxRepository.getOutbox("2", "PAYMENT", "PAYMENT_SUCCESS").orElse(null);
        assertFalse(outbox2.isSent());
        assertNull(outbox2.getSentAt());
    }

}
