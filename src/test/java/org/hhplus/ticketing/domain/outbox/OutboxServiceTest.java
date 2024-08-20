package org.hhplus.ticketing.domain.outbox;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.common.messaging.MessageSender;
import org.hhplus.ticketing.domain.outbox.model.Outbox;
import org.hhplus.ticketing.domain.outbox.model.OutboxCommand;
import org.hhplus.ticketing.domain.outbox.model.constants.OutboxConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 아웃박스 서비스 단위테스트입니다.
public class OutboxServiceTest {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private MessageSender messageSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("🟢 [아웃박스_저장_테스트]")
    void saveTest_저장_후_저장된_정보가_리턴된다() {
        // Given
        OutboxCommand.Save command = new OutboxCommand.Save("1", "PAYMENT", "PAYMENT_SUCCESS", "MESSAGE");
        Outbox expectedOutbox = Outbox.from(command);

        given(outboxRepository.save(any(Outbox.class))).willReturn(expectedOutbox);

        // When
        Outbox result = outboxService.save(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedOutbox, result);
        verify(outboxRepository, times(1)).save(any(Outbox.class));
    }

    @Test
    @DisplayName("🟢 [아웃박스_상태변경_테스트]")
    void updateSentTest_발행상태로_변경된다() {
        // Given
        OutboxCommand.UpdateSent command = new OutboxCommand.UpdateSent("1", "PAYMENT", "PAYMENT_SUCCESS");
        Outbox outbox = Outbox.builder()
                .outboxId(1L)
                .messageKey("1")
                .domainType("PAYMENT")
                .eventType("PAYMENT_SUCCESS")
                .message("MESSAGE")
                .isSent(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(outboxRepository.getOutbox(anyString(), anyString(), anyString())).willReturn(Optional.of(outbox));
        given(outboxRepository.save(any(Outbox.class))).willReturn(outbox);

        // When
        Outbox result = outboxService.updateSent(command);

        // Then
        assertNotNull(result);
        assertTrue(result.isSent());
        assertNotNull(result.getSentAt());
        verify(outboxRepository, times(1)).getOutbox(anyString(), anyString(), anyString());
        verify(outboxRepository, times(1)).save(any(Outbox.class));
    }

    @Test
    @DisplayName("🔴 [아웃박스_상태변경_테스트]")
    public void updateSentTest_이미_발행된_상태일_경우_INVALID_STATE_예외반환() {
        // Given
        OutboxCommand.UpdateSent command = new OutboxCommand.UpdateSent("1", "PAYMENT", "PAYMENT_SUCCESS");
        Outbox outbox = Outbox.builder()
                .outboxId(1L)
                .messageKey("1")
                .domainType("PAYMENT")
                .eventType("PAYMENT_SUCCESS")
                .message("MESSAGE")
                .isSent(true)
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        given(outboxRepository.getOutbox(anyString(), anyString(), anyString())).willReturn(Optional.ofNullable(outbox));

        // Then
        assertThatThrownBy(() -> outboxService.updateSent(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_STATE);
    }

    @Test
    @DisplayName("🔴 [아웃박스_상태변경_테스트]")
    public void updateSentTest_조회된_아웃박스_데이터가_없을경우_OUTBOX_NOT_FOUND_예외반환() {
        // Given
        OutboxCommand.UpdateSent command = new OutboxCommand.UpdateSent("1", "PAYMENT", "PAYMENT_SUCCESS");

        given(outboxRepository.getOutbox(anyString(), anyString(), anyString())).willReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> outboxService.updateSent(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.OUTBOX_NOT_FOUND);
    }

    @Test
    @DisplayName("🟢 [메시지_재발송_테스트]")
    void retryFailedMessages_실패한_메시지를_재발송하고_발송상태로_변경된다() {
        // Given
        Outbox outbox = Outbox.builder()
                .outboxId(1L)
                .messageKey("1")
                .domainType("PAYMENT")
                .eventType("PAYMENT_SUCCESS")
                .message("MESSAGE")
                .isSent(false)
                .createdAt(LocalDateTime.now().minusMinutes(OutboxConstants.OUTBOX_RETRY_THRESHOLD_MINUTES + 1))
                .build();

        given(outboxRepository.findAllNotPublishedOutBoxByTime(any(LocalDateTime.class))).willReturn(Collections.singletonList(outbox));
        given(messageSender.sendMessage(anyString(), anyString(), anyString())).willReturn(CompletableFuture.completedFuture(true));
        given(outboxRepository.save(any(Outbox.class))).willReturn(outbox);

        // When
        outboxService.retryFailedMessages();

        // Then
        verify(outboxRepository, times(1)).findAllNotPublishedOutBoxByTime(any(LocalDateTime.class));
        verify(messageSender, times(1)).sendMessage(anyString(), anyString(), anyString());
        verify(outboxRepository, times(1)).save(any(Outbox.class));
        assertTrue(outbox.isSent());
        assertNotNull(outbox.getSentAt());
    }

    @Test
    @DisplayName("🔴 [메시지_재발송_테스트]")
    void retryFailedMessages_메시지_발송에_실패한_경우_발송상태로_변경되지_않는다() {
        // Given
        Outbox existingOutbox = Outbox.builder()
                .outboxId(1L)
                .messageKey("1")
                .domainType("PAYMENT")
                .eventType("PAYMENT_SUCCESS")
                .message("MESSAGE")
                .isSent(false)
                .createdAt(LocalDateTime.now().minusMinutes(OutboxConstants.OUTBOX_RETRY_THRESHOLD_MINUTES + 1))
                .build();

        given(outboxRepository.findAllNotPublishedOutBoxByTime(any(LocalDateTime.class))).willReturn(Collections.singletonList(existingOutbox));
        given(messageSender.sendMessage(anyString(), anyString(), anyString())).willReturn(CompletableFuture.completedFuture(false));

        // When
        outboxService.retryFailedMessages();

        // Then
        verify(outboxRepository, times(1)).findAllNotPublishedOutBoxByTime(any(LocalDateTime.class));
        verify(messageSender, times(1)).sendMessage(anyString(), anyString(), anyString());
        verify(outboxRepository, never()).save(existingOutbox); // 메시지 전송이 실패했으므로 save 메서드 호출 x
        assertFalse(existingOutbox.isSent());
        assertNull(existingOutbox.getSentAt());
    }
}