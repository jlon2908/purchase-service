package com.arka.purchase_service.application.ports.out;

import reactor.core.publisher.Mono;

public interface CartCommandPort {
    Mono<Void> clearCart(String userId, String jwtToken);
}

