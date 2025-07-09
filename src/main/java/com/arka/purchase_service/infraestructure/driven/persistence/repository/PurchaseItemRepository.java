package com.arka.purchase_service.infraestructure.driven.persistence.repository;

import com.arka.purchase_service.infraestructure.driven.persistence.entity.PurchaseItemEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface PurchaseItemRepository extends ReactiveCrudRepository<PurchaseItemEntity, UUID> {
    @Query("INSERT INTO purchase_item (id, purchase_id, sku, quantity, unit_price, subtotal) " +
           "VALUES (:id, :purchaseId, :sku, :quantity, :unitPrice, :subtotal) " +
           "ON CONFLICT (id) DO UPDATE SET " +
           "purchase_id = EXCLUDED.purchase_id, sku = EXCLUDED.sku, quantity = EXCLUDED.quantity, unit_price = EXCLUDED.unit_price, subtotal = EXCLUDED.subtotal")
    Mono<Void> upsertPurchaseItem(UUID id, UUID purchaseId, String sku, int quantity, BigDecimal unitPrice, BigDecimal subtotal);
}
