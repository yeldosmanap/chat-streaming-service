package com.kcell.testtask.messaging.config;

import com.kcell.testtask.messaging.model.Message;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, Message> consumerFactory(KafkaProperties kafkaProperties){
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }
}
