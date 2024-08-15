package org.hhplus.ticketing.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.hhplus.ticketing.domain.common.messaging.MessageSender;
import org.hhplus.ticketing.support.config.KafkaConsumerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageSenderIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        String testGroupId = "test-group-1";
        ConsumerFactory<String, String> consumerFactory = kafkaConsumerConfig.createConsumerFactory(testGroupId);
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList("PAYMENT_SUCCESS"));
    }

    @Test
    @DisplayName("🟢 메시지_발행_테스트_메시지가_정상적으로_발행_및_수신된다")
    public void sendMessageTest_메시지_발행_테스트_메시지가_정상적으로_발행_및_수신된다() throws InterruptedException {
        // Given
        String messageKey = "1";
        String eventType = "PAYMENT_SUCCESS";
        String message = "{\"token\":\"testToken\",\"userId\":1,\"reservationId\":1001,\"price\":150}";

        CountDownLatch latch = new CountDownLatch(1);

        // When
        CompletableFuture<Boolean> future = messageSender.sendMessage(eventType, messageKey, message);

        future.thenAccept(success -> {
            if (success) {
                latch.countDown(); // 작업 완료 시 카운트를 감소
            }
        });

        // latch가 10초 내에 0이 될 때까지 대기
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "비동기 작업이 완료되지 않았습니다.");

        // Then
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10)); // 메시지 수신 대기
        assertEquals(1, records.count());                                       // 메시지가 1개 수신되었는지 확인

        for (ConsumerRecord<String, String> record : records) {
            assertEquals(messageKey, record.key());
            assertEquals(message, record.value());
        }
    }
}