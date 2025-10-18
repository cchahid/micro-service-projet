package com.buberDinner.userService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        // Utiliser uniquement host:port
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configs.put(AdminClientConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
//        configs.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
//        return new KafkaAdmin(configs);
//    }


    @Bean
    public NewTopic guestTopic() {
        return TopicBuilder
                .name("guest-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic hostTopic(){
        return TopicBuilder
                .name("host-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}