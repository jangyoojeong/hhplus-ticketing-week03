package org.hhplus.ticketing.domain.common.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class MessageSenderTest {

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private MessageSender messageSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("🟢 [메시지_전송_테스트]")
    void sendMessage_메시지_전송에_성공한다() {
        // Given
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.set(mock(SendResult.class));
        given(messageProducer.send(anyString(), anyString(), anyString())).willReturn(future.completable());

        // When
        CompletableFuture<Boolean> result = messageSender.sendMessage("eventType", "messageKey", "message");

        // Then
        assertTrue(result.join()); // 메시지 전송 성공
        verify(messageProducer, times(1)).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("🔴 [메시지_전송_테스트]")
    void sendMessage_메시지_전송에_실패한다() {
        // Given
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Kafka send failure"));
        given(messageProducer.send(anyString(), anyString(), anyString())).willReturn(future.completable());

        // When
        CompletableFuture<Boolean> result = messageSender.sendMessage("eventType", "messageKey", "message");

        // Then
        assertFalse(result.join()); // 메시지 전송 실패
        verify(messageProducer, times(1)).send(anyString(), anyString(), anyString());
    }
}