package org.hhplus.ticketing.support.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.payment-success}")
    private String successTopic;

    @Bean
    public NewTopic paymentSuccessTopic() {
        return new NewTopic(successTopic, 1, (short) 1);
    }
}
