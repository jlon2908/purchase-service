package com.arka.purchase_service.infraestructure.driven.cart.rabbit.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {
    private String orderCode;
    private String userId;
    private String warehousePickupCode;
    private List<Item> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String sku;
        private List<AssignedDistribution> assignedDistributions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignedDistribution {
        private String warehouseCode;
        private int quantity;
    }
}

