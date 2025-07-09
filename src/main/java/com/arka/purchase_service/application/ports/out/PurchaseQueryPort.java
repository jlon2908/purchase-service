package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.Purchase;
import reactor.core.publisher.Mono;

public interface PurchaseQueryPort {
    Mono<Purchase> findByOrderCode(String orderCode);
}

