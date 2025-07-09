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
@Table("purchase")
public class PurchaseEntity {
    @Id
    @Column("id")
    private UUID id;
    @Column("order_code")
    private String orderCode;
    @Column("client_id")
    private UUID clientId;
    @Column("warehouse_pickup")
    private String warehousePickup;
    @Column("purchase_date")
    private LocalDateTime purchaseDate;
    @Column("status")
    private String status;
    @Column("total_amount")
    private BigDecimal totalAmount;
}
