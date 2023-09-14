package com.kcell.testtask.messaging.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcell.testtask.messaging.dto.kafka.MessageDto;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class KafkaProducerValueSerializer implements Serializer<MessageDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, MessageDto message) {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Message", e);
        }
    }

    @Override
    public void close() {
    }
}
