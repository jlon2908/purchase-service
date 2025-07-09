package com.arka.purchase_service.infraestructure.driven.persistence.repository;

import com.arka.purchase_service.infraestructure.driven.persistence.entity.PurchaseEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends ReactiveCrudRepository<PurchaseEntity, UUID> {
    @Query("INSERT INTO purchase (id, order_code, client_id, warehouse_pickup, purchase_date, status, total_amount) " +
           "VALUES (:id, :orderCode, :clientId, :warehousePickup, :purchaseDate, :status, :totalAmount) " +
           "ON CONFLICT (id) DO UPDATE SET " +
           "order_code = EXCLUDED.order_code, client_id = EXCLUDED.client_id, warehouse_pickup = EXCLUDED.warehouse_pickup, " +
           "purchase_date = EXCLUDED.purchase_date, status = EXCLUDED.status, total_amount = EXCLUDED.total_amount")
    Mono<Void> upsertPurchase(UUID id, String orderCode, UUID clientId, String warehousePickup, LocalDateTime purchaseDate, String status, BigDecimal totalAmount);

    @Query("SELECT * FROM purchase WHERE order_code = :orderCode")
    Mono<PurchaseEntity> findByOrderCode(String orderCode);
}
