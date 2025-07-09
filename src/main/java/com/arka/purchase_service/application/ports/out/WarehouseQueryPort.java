package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.Warehouse;
import reactor.core.publisher.Flux;

public interface WarehouseQueryPort {
    Flux<Warehouse> getAllWarehouses();
}

