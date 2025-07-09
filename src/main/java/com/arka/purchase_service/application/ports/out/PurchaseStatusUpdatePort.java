package com.arka.purchase_service.application.ports.out;

import reactor.core.publisher.Mono;

public interface PurchaseStatusUpdatePort {
    Mono<Void> updateStatusByOrderCode(String orderCode, String newStatus);
}

