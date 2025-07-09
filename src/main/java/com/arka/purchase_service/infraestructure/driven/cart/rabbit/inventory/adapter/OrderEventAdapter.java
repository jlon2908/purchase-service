package com.arka.purchase_service.infraestructure.driven.cart.rabbit.inventory.adapter;

import com.arka.purchase_service.application.ports.out.OrderEventPort;
import com.arka.purchase_service.domain.model.Purchase;
import com.arka.purchase_service.infraestructure.driven.cart.rabbit.inventory.dto.OrderPlacedEvent;
import com.arka.purchase_service.infraestructure.driven.cart.rabbit.inventory.mapper.OrderPlacedEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderEventAdapter implements OrderEventPort {
    private final RabbitTemplate rabbitTemplate;
    private final OrderPlacedEventMapper mapper;
    private static final String EXCHANGE = "order";
    private static final String ROUTING_KEY = "order.placed";

    @Override
    public Mono<Void> publishOrderPlacedEvent(Purchase purchase) {
        return Mono.fromRunnable(() -> {
            OrderPlacedEvent event = mapper.toEvent(purchase);
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
        });
    }
}

