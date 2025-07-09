package com.arka.purchase_service.infraestructure.driven.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("purchase_item_distribution")
public class PurchaseItemDistributionEntity {
    @Id
    @Column("id")
    private UUID id;
    @Column("purchase_item_id")
    private UUID purchaseItemId;
    @Column("warehouse_code")
    private String warehouseCode;
    @Column("quantity")
    private int quantity;
}
