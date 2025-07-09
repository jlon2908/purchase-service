package com.arka.purchase_service.application.ports.in;

import com.arka.purchase_service.domain.model.Purchase;
import com.arka.purchase_service.domain.model.ShippingAddress;
import reactor.core.publisher.Mono;

public interface StartPurchasePort {
    Mono<Purchase> startPurchase(ShippingAddress shippingAddress, String userId, String jwtToken);
}

