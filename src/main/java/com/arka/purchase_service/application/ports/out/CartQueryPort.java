package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.CartItem;
import reactor.core.publisher.Flux;

public interface CartQueryPort {
    Flux<CartItem> getCartItems(String userId, String jwtToken);
}

