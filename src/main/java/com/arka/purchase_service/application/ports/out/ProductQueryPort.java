package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.ProductPrice;
import reactor.core.publisher.Mono;

public interface ProductQueryPort {
    Mono<ProductPrice> getProductPriceBySku(String sku);
}

