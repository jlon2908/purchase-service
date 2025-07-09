package com.arka.purchase_service.infraestructure.driven.cart.rabbit.inventory.mapper;

import com.arka.purchase_service.domain.model.Purchase;
import com.arka.purchase_service.domain.model.PurchaseItem;
import com.arka.purchase_service.domain.model.PurchaseItemDistribution;
import com.arka.purchase_service.infraestructure.driven.cart.rabbit.inventory.dto.OrderPlacedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderPlacedEventMapper {
    public OrderPlacedEvent toEvent(Purchase purchase) {
        return OrderPlacedEvent.builder()
                .orderCode(purchase.getOrderCode())
                .userId(purchase.getClientId().toString())
                .warehousePickupCode(purchase.getWarehousePickup())
                .items(purchase.getItems().stream().map(this::toItem).collect(Collectors.toList()))
                .build();
    }

    private OrderPlacedEvent.Item toItem(PurchaseItem item) {
        return OrderPlacedEvent.Item.builder()
                .sku(item.getSku())
                .assignedDistributions(item.getDistributions() != null ?
                        item.getDistributions().stream()
                                .filter(d -> d.getQuantity() > 0)
                                .map(this::toDistribution)
                                .collect(Collectors.toList()) :
                        List.of())
                .build();
    }

    private OrderPlacedEvent.AssignedDistribution toDistribution(PurchaseItemDistribution dist) {
        return OrderPlacedEvent.AssignedDistribution.builder()
                .warehouseCode(dist.getWarehouseCode())
                .quantity(dist.getQuantity())
                .build();
    }
}
