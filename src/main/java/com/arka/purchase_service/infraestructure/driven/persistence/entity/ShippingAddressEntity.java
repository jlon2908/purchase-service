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
@Table("shipping_address")
public class ShippingAddressEntity {
    @Id
    @Column("id")
    private UUID id;
    @Column("purchase_id")
    private UUID purchaseId;
    @Column("street")
    private String street;
    @Column("city")
    private String city;
    @Column("state")
    private String state;
    @Column("country")
    private String country;
    @Column("notes")
    private String notes;
}
