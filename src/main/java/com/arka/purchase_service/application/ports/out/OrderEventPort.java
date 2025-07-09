package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.Purchase;
import reactor.core.publisher.Mono;

public interface OrderEventPort {
    Mono<Void> publishOrderPlacedEvent(Purchase purchase);
}
