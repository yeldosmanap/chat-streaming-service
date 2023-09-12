package com.kcell.testtask.messaging.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcell.testtask.messaging.model.Message;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KafkaConsumerValueDeserializer implements Deserializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }

            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), Message.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto");
        }
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
