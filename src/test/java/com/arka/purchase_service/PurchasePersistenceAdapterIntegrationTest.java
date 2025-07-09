package com.arka.purchase_service;

import com.arka.purchase_service.domain.model.*;
import com.arka.purchase_service.infraestructure.driven.persistence.adapter.PurchasePersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class PurchasePersistenceAdapterIntegrationTest {
    @Autowired
    private PurchasePersistenceAdapter purchasePersistenceAdapter;

    @Test
    void savePurchase_persistsPurchaseAndReturnsIt() {
        ShippingAddress address = ShippingAddress.builder()
                .id(UUID.randomUUID())
                .street("Calle 123")
                .city("Ciudad")
                .state("Estado")
                .country("País")
                .notes("Ninguna")
                .build();
        PurchaseItemDistribution dist = PurchaseItemDistribution.builder()
                .id(UUID.randomUUID())
                .warehouseCode("W1")
                .quantity(2)
                .build();
        PurchaseItem item = PurchaseItem.builder()
                .id(UUID.randomUUID())
                .sku("sku1")
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .subtotal(BigDecimal.valueOf(20))
                .distributions(List.of(dist))
                .build();
        Purchase purchase = Purchase.builder()
                .id(UUID.randomUUID())
                .orderCode("ORD-TEST")
                .clientId(UUID.randomUUID())
                .warehousePickup("W1")
                .purchaseDate(java.time.LocalDateTime.now())
                .status("PENDING")
                .totalAmount(BigDecimal.valueOf(20))
                .shippingAddress(address)
                .items(List.of(item))
                .build();

        StepVerifier.create(purchasePersistenceAdapter.savePurchase(purchase))
                .expectNextMatches(saved -> saved.getOrderCode().equals("ORD-TEST") && saved.getItems().size() == 1)
                .verifyComplete();
    }
}


