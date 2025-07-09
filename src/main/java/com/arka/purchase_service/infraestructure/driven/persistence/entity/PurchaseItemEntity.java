package com.arka.purchase_service.infraestructure.driven.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("purchase_item")
public class PurchaseItemEntity {
    @Id
    @Column("id")
    private UUID id;
    @Column("purchase_id")
    private UUID purchaseId;
    @Column("sku")
    private String sku;
    @Column("quantity")
    private int quantity;
    @Column("unit_price")
    private BigDecimal unitPrice;
    @Column("subtotal")
    private BigDecimal subtotal;
}
