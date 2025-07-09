package com.arka.purchase_service.infraestructure.driven.persistence.repository;

import com.arka.purchase_service.infraestructure.driven.persistence.entity.PurchaseItemDistributionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PurchaseItemDistributionRepository extends ReactiveCrudRepository<PurchaseItemDistributionEntity, UUID> {
    @Query("INSERT INTO purchase_item_distribution (id, purchase_item_id, warehouse_code, quantity) " +
           "VALUES (:id, :purchaseItemId, :warehouseCode, :quantity) " +
           "ON CONFLICT (id) DO UPDATE SET " +
           "purchase_item_id = EXCLUDED.purchase_item_id, warehouse_code = EXCLUDED.warehouse_code, quantity = EXCLUDED.quantity")
    Mono<Void> upsertPurchaseItemDistribution(UUID id, UUID purchaseItemId, String warehouseCode, int quantity);
}
