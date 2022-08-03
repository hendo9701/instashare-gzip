package com.instashare.instasharegzip;

import com.instashare.instasharegzip.services.ConsumeMessageService;
import com.instashare.instasharegzip.util.DestinationInfo;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class Amqp {

  private final AmqpAdmin amqpAdmin;

  private final DestinationInfo destinationInfo;

  @PostConstruct
  public void setup() {
    val exchange = ExchangeBuilder.directExchange(destinationInfo.exchange()).durable(true).build();
    amqpAdmin.declareExchange(exchange);
    val queue = QueueBuilder.durable(destinationInfo.routingKey()).build();
    amqpAdmin.declareQueue(queue);
    val binding =
        BindingBuilder.bind(queue).to(exchange).with(destinationInfo.routingKey()).noargs();
    amqpAdmin.declareBinding(binding);
  }

  @Bean
  SimpleMessageListenerContainer container(
      ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(destinationInfo.routingKey());
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(ConsumeMessageService receiver) {
    return new MessageListenerAdapter(receiver, "consumeMessage");
  }
}
