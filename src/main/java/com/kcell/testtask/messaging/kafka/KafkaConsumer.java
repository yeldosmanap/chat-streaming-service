package com.kcell.testtask.messaging.kafka;

import com.kcell.testtask.messaging.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.consumer.topic}")
    public void listen(Message message) {
        log.info("Message received by listener: {} | {}", message.getContent(), message.getTimestamp());
    }
}
