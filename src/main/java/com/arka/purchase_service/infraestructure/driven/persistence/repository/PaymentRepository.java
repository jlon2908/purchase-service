package com.arka.purchase_service.infraestructure.driven.persistence.repository;

import com.arka.purchase_service.infraestructure.driven.persistence.entity.PaymentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<PaymentEntity, UUID> {
    @Query("INSERT INTO payment (id, purchase_id, payment_method, amount, payment_date, status, transaction_code) " +
           "VALUES (:id, :purchaseId, :paymentMethod, :amount, :paymentDate, :status, :transactionCode) " +
           "ON CONFLICT (id) DO UPDATE SET " +
           "purchase_id = EXCLUDED.purchase_id, payment_method = EXCLUDED.payment_method, amount = EXCLUDED.amount, payment_date = EXCLUDED.payment_date, status = EXCLUDED.status, transaction_code = EXCLUDED.transaction_code")
    Mono<Void> upsertPayment(UUID id, UUID purchaseId, String paymentMethod, BigDecimal amount, LocalDateTime paymentDate, String status, String transactionCode);
}
