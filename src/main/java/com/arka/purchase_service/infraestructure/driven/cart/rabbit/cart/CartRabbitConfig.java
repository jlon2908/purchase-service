package com.arka.purchase_service.infraestructure.driven.cart.rabbit.cart;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartRabbitConfig {
    public static final String CART_CLEAR_QUEUE = "cart.clear.queue";
    public static final String CART_CLEAR_EXCHANGE = "cart.clear.exchange";
    public static final String CART_CLEAR_ROUTING_KEY = "cart.clear";

    @Bean
    public Queue cartClearQueue() {
        return new Queue(CART_CLEAR_QUEUE, true);
    }

    @Bean
    public DirectExchange cartClearExchange() {
        return new DirectExchange(CART_CLEAR_EXCHANGE);
    }

    @Bean
    public Binding cartClearBinding() {
        return BindingBuilder.bind(cartClearQueue())
                .to(cartClearExchange())
                .with(CART_CLEAR_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}

