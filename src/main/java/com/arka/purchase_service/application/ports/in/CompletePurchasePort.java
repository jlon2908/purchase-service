package com.arka.purchase_service.application.ports.in;

import reactor.core.publisher.Mono;

public interface CompletePurchasePort {
    Mono<Void> completePurchase(String orderCode);
}

