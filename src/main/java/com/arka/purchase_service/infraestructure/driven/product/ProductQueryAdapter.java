package com.arka.purchase_service.infraestructure.driven.product;

import com.arka.purchase_service.application.ports.out.ProductQueryPort;
import com.arka.purchase_service.domain.model.ProductPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductQueryAdapter implements ProductQueryPort {
    @Value("${catalog.base-url}")
    private String catalogBaseUrl;

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<ProductPrice> getProductPriceBySku(String sku) {
        return webClientBuilder.build()
                .get()
                .uri(catalogBaseUrl + "/api/products/filter/sku?sku=" + sku)
                .retrieve()
                .bodyToFlux(ProductPrice.class)
                .next(); // Toma el primer elemento del array
    }
}
