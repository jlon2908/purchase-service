package com.arka.purchase_service.infraestructure.driven.cart.rabbit.cart;

import com.arka.purchase_service.application.ports.out.CartCommandPort;
import com.arka.purchase_service.infraestructure.driven.cart.dto.CartClearEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CartCommandAdapter implements CartCommandPort {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> clearCart(String userId, String jwtToken) {
        return Mono.fromRunnable(() -> {
            CartClearEvent event = CartClearEvent.builder().userId(userId).build();
            rabbitTemplate.convertAndSend(
                CartRabbitConfig.CART_CLEAR_EXCHANGE,
                CartRabbitConfig.CART_CLEAR_ROUTING_KEY,
                event
            );
        });
    }
}
