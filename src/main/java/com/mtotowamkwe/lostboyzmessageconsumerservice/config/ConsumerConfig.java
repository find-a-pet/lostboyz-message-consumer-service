package com.mtotowamkwe.lostboyzmessageconsumerservice.config;

import com.mtotowamkwe.lostboyzmessageconsumerservice.api.MessageConsumer;
import com.mtotowamkwe.lostboyzmessageconsumerservice.api.impl.MessageConsumerImpl;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.mtotowamkwe.lostboyzmessageconsumerservice"})
public class ConsumerConfig {

    @Bean
    public Queue queue() {
        return new Queue("account.verification.emails");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("account.verification");
    }

    @Bean
    public Binding binding(DirectExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("payload");
    }

    @Bean
    public MessageConsumer receiver() {
        return new MessageConsumerImpl();
    }

}