package com.arka.purchase_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    private UUID id;
    private String orderCode;
    private UUID clientId;
    private String warehousePickup;
    private LocalDateTime purchaseDate;
    private String status;
    private BigDecimal totalAmount;
    private ShippingAddress shippingAddress;
    private List<PurchaseItem> items;
    private String stripeCheckoutUrl;
}
