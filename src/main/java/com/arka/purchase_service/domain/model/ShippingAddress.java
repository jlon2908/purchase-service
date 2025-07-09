package com.arka.purchase_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private UUID id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String notes;
}

