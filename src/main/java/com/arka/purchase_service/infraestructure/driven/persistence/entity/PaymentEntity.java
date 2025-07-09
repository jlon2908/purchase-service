package com.arka.purchase_service.infraestructure.driven.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("payment")
public class PaymentEntity {
    @Id
    @Column("id")
    private UUID id;
    @Column("purchase_id")
    private UUID purchaseId;
    @Column("payment_method")
    private String paymentMethod;
    @Column("amount")
    private BigDecimal amount;
    @Column("payment_date")
    private LocalDateTime paymentDate;
    @Column("status")
    private String status;
    @Column("transaction_code")
    private String transactionCode;
}
