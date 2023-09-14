package com.kcell.testtask.messaging.service.implementations;

import com.kcell.testtask.messaging.dto.kafka.MessageDto;
import com.kcell.testtask.messaging.kafka.producer.KafkaProducer;
import com.kcell.testtask.messaging.service.MessageSenderService;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerMessageSenderService implements MessageSenderService {
    private final KafkaProducer kafkaProducer;

    public KafkaProducerMessageSenderService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void sendMessage(MessageDto message) {
        kafkaProducer.sendMessage(message);
    }
}
