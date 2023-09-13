package com.kcell.testtask.messaging.config;

import com.kcell.testtask.messaging.model.Message;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public KafkaConsumer<String, Message> kafkaConsumer(KafkaProperties kafkaProperties){
        var consumerProperties = kafkaProperties.buildConsumerProperties();
        return new KafkaConsumer<>(consumerProperties);
    }
}
