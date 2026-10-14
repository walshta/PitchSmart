package org.walshta.pitchsmart.ingest.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.walshta.pitchsmart.events.Topics;

@Configuration
public class KafkaTopicConfig {

  @Bean
  NewTopic gameEvents() {
    return TopicBuilder.name(Topics.GAME_EVENTS)
        .partitions(12)
        .replicas(1)
        .build();
  }
}
