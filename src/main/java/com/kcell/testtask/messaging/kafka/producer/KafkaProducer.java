package com.kcell.testtask.messaging.kafka.producer;

import com.kcell.testtask.messaging.dto.kafka.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {
    @Value("${spring.kafka.producer.topic}")
    private String topic;
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, MessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(MessageDto message) {
        try {
            kafkaTemplate.send(topic, message);
            log.info("Message sent to kafka topic: {}", topic);
        } catch (Exception e) {
            log.error("Exception while sending message to kafka topic: {}", topic, e);
            throw new RuntimeException(e);
        }
    }
}
