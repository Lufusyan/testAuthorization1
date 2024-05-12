package com.generation.cdr.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    public NewTopic newTopic() {
        return new NewTopic("cdr-records", 1, (short) 1); //TODO получать топик по-другому
    }
}
