package org.hhplus.ticketing.support.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PubSubListener {

    @Autowired
    private RedissonClient redissonClient;

    private static final String PUBSUB_TOPIC = "lockTopic";

    @PostConstruct
    public void init() {
        redissonClient.getTopic(PUBSUB_TOPIC).addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String message) {
                handleMessage(message);
            }
        });
    }

    private void handleMessage(String message) {
        log.info("Received Pub/Sub message: {}", message);
    }
}
