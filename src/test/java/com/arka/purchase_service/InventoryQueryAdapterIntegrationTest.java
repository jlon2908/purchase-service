package com.arka.purchase_service;

import com.arka.purchase_service.application.ports.out.InventoryQueryPort;
import com.arka.purchase_service.domain.model.InventoryStock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class InventoryQueryAdapterIntegrationTest {
    @MockBean
    private InventoryQueryPort inventoryQueryPort;

    @Test
    void getStockBySku_returnsInventoryStock() {
        String sku = "sku1";
        InventoryStock stock = InventoryStock.builder()
                .sku(sku)
                .available(10)
                .warehouseStock(Map.of("W1", 10))
                .build();
        when(inventoryQueryPort.getStockBySku(sku)).thenReturn(Mono.just(stock));

        StepVerifier.create(inventoryQueryPort.getStockBySku(sku))
                .expectNextMatches(s -> s.getSku().equals(sku) && s.getAvailable() == 10)
                .verifyComplete();
    }
}
