package com.arka.purchase_service.infraestructure.driven.persistence.adapter;

import com.arka.purchase_service.application.ports.out.PurchaseStatusUpdatePort;
import com.arka.purchase_service.infraestructure.driven.persistence.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PurchaseStatusUpdateAdapter implements PurchaseStatusUpdatePort {
    private final PurchaseRepository purchaseRepository;

    @Override
    public Mono<Void> updateStatusByOrderCode(String orderCode, String newStatus) {
        // Busca la compra por orderCode y actualiza el estado
        return purchaseRepository.findByOrderCode(orderCode)
                .flatMap(purchaseEntity -> {
                    purchaseEntity.setStatus(newStatus);
                    return purchaseRepository.upsertPurchase(
                            purchaseEntity.getId(),
                            purchaseEntity.getOrderCode(),
                            purchaseEntity.getClientId(),
                            purchaseEntity.getWarehousePickup(),
                            purchaseEntity.getPurchaseDate(),
                            newStatus,
                            purchaseEntity.getTotalAmount()
                    );
                });
    }
}

