package com.instashare.instasharegzip.config;

import com.instashare.instasharegzip.util.DestinationInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Bean
  public DestinationInfo destinationInfo(
      @Value("${amqp.exchange}") String exchange, @Value("${amqp.routing-key}") String routingKey) {
    return new DestinationInfo(exchange, routingKey);
  }
}
