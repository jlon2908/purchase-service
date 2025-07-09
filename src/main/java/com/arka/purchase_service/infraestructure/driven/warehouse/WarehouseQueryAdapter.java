package com.arka.purchase_service.infraestructure.driven.warehouse;

import com.arka.purchase_service.application.ports.out.WarehouseQueryPort;
import com.arka.purchase_service.domain.model.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class WarehouseQueryAdapter implements WarehouseQueryPort {
    @Value("${inventory.base-url}")
    private String inventoryBaseUrl;

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<Warehouse> getAllWarehouses() {
        return webClientBuilder.build()
                .get()
                .uri(inventoryBaseUrl + "/api/warehouses")
                .retrieve()
                .bodyToFlux(Warehouse.class)
                .log("WAREHOUSE-DEBUG")
                .doOnNext(w -> System.out.println("Warehouse recibido: " + w))
                .doOnError(e -> {
                    System.err.println("Error al obtener bodegas: " + e.getMessage());
                    e.printStackTrace();
                })
                ;

    }
}

