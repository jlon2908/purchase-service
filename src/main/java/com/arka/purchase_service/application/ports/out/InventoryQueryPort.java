package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.InventoryStock;
import reactor.core.publisher.Mono;

public interface InventoryQueryPort {
    Mono<InventoryStock> getStockBySku(String sku);
}

