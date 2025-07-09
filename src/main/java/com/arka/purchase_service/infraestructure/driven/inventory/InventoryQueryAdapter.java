package com.arka.purchase_service.infraestructure.driven.inventory;

import com.arka.purchase_service.application.ports.out.InventoryQueryPort;
import com.arka.purchase_service.domain.model.InventoryStock;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import lombok.Data;

@Component
@RequiredArgsConstructor
public class InventoryQueryAdapter implements InventoryQueryPort {
    @Value("${inventory.base-url}")
    private String inventoryBaseUrl;

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<InventoryStock> getStockBySku(String sku) {
        return webClientBuilder.build()
                .get()
                .uri(inventoryBaseUrl + "/api/inventory/" + sku + "/stock")
                .retrieve()
                .bodyToFlux(InventoryStockResponse.class)
                .collectList()
                .map(list -> {
                    Map<String, Integer> warehouseStock = new HashMap<>();
                    int total = 0;
                    for (InventoryStockResponse resp : list) {
                        warehouseStock.put(resp.getWarehouseCode(), resp.getQuantity());
                        total += resp.getQuantity();
                    }
                    return InventoryStock.builder()
                            .sku(sku)
                            .available(total)
                            .warehouseStock(warehouseStock)
                            .build();
                });
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class InventoryStockResponse {
        private String sku;
        private String warehouseCode;
        private int quantity;
        // lastUpdated ignorado
    }
}
