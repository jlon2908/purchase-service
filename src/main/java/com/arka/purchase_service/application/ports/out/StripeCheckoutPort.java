package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.PurchaseItem;
import reactor.core.publisher.Mono;
import java.util.List;

public interface StripeCheckoutPort {
    Mono<String> createCheckoutSession(List<PurchaseItem> items, String orderCode);
}

