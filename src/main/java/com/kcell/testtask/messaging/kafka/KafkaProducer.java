package com.kcell.testtask.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    @Value("${spring.kafka.producer.topic}")
    private String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        try {
            kafkaTemplate.send(topic, message);
            logger.info("Message sent to kafka topic: " + topic);
        } catch (Exception e) {
            logger.error("Exception while sending message to kafka topic: " + topic, e);

            throw new RuntimeException();
        }
    }
}
