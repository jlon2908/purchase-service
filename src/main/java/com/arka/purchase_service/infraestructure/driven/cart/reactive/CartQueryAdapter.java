package com.arka.purchase_service.infraestructure.driven.cart.reactive;

import com.arka.purchase_service.application.ports.out.CartQueryPort;
import com.arka.purchase_service.domain.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class CartQueryAdapter implements CartQueryPort {
    @Value("${cart.base-url}")
    private String cartBaseUrl;

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<CartItem> getCartItems(String userId, String jwtToken) {
        return webClientBuilder.build()
                .get()
                .uri(cartBaseUrl + "/cart/items")
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(CartItem.class);
    }
}

