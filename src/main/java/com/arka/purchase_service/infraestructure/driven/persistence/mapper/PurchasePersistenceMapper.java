package com.arka.purchase_service.infraestructure.driven.persistence.mapper;

import com.arka.purchase_service.domain.model.*;
import com.arka.purchase_service.infraestructure.driven.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PurchasePersistenceMapper {
    // Purchase <-> PurchaseEntity
    public PurchaseEntity toEntity(Purchase purchase) {
        return PurchaseEntity.builder()
                .id(purchase.getId())
                .orderCode(purchase.getOrderCode())
                .clientId(purchase.getClientId())
                .warehousePickup(purchase.getWarehousePickup())
                .purchaseDate(purchase.getPurchaseDate())
                .status(purchase.getStatus())
                .totalAmount(purchase.getTotalAmount())
                .build();
    }

    public Purchase toDomain(PurchaseEntity entity, ShippingAddress shippingAddress, List<PurchaseItem> items) {
        return Purchase.builder()
                .id(entity.getId())
                .orderCode(entity.getOrderCode())
                .clientId(entity.getClientId())
                .warehousePickup(entity.getWarehousePickup())
                .purchaseDate(entity.getPurchaseDate())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .shippingAddress(shippingAddress)
                .items(items)
                .build();
    }

    // ShippingAddress <-> ShippingAddressEntity
    public ShippingAddressEntity toEntity(ShippingAddress address, UUID purchaseId) {
        return ShippingAddressEntity.builder()
                .id(address.getId())
                .purchaseId(purchaseId)
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .notes(address.getNotes())
                .build();
    }

    public ShippingAddress toDomain(ShippingAddressEntity entity) {
        return ShippingAddress.builder()
                .id(entity.getId())
                .street(entity.getStreet())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .notes(entity.getNotes())
                .build();
    }

    // PurchaseItem <-> PurchaseItemEntity
    public PurchaseItemEntity toEntity(PurchaseItem item, UUID purchaseId) {
        return PurchaseItemEntity.builder()
                .id(item.getId())
                .purchaseId(purchaseId)
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public PurchaseItem toDomain(PurchaseItemEntity entity, List<PurchaseItemDistribution> distributions) {
        return PurchaseItem.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .distributions(distributions)
                .build();
    }

    // PurchaseItemDistribution <-> PurchaseItemDistributionEntity
    public PurchaseItemDistributionEntity toEntity(PurchaseItemDistribution dist, UUID purchaseItemId) {
        return PurchaseItemDistributionEntity.builder()
                .id(dist.getId())
                .purchaseItemId(purchaseItemId)
                .warehouseCode(dist.getWarehouseCode())
                .quantity(dist.getQuantity())
                .build();
    }

    public PurchaseItemDistribution toDomain(PurchaseItemDistributionEntity entity) {
        return PurchaseItemDistribution.builder()
                .id(entity.getId())
                .warehouseCode(entity.getWarehouseCode())
                .quantity(entity.getQuantity())
                .build();
    }

    // Métodos de lista
    public List<PurchaseItemEntity> toItemEntities(List<PurchaseItem> items, UUID purchaseId) {
        return items.stream().map(i -> toEntity(i, purchaseId)).collect(Collectors.toList());
    }
    public List<PurchaseItem> toItemDomains(List<PurchaseItemEntity> entities, List<List<PurchaseItemDistribution>> distributions) {
        return entities.stream().map(e -> toDomain(e, distributions.get(entities.indexOf(e)))).collect(Collectors.toList());
    }
    public List<PurchaseItemDistributionEntity> toDistributionEntities(List<PurchaseItemDistribution> dists, UUID purchaseItemId) {
        return dists.stream().map(d -> toEntity(d, purchaseItemId)).collect(Collectors.toList());
    }
    public List<PurchaseItemDistribution> toDistributionDomains(List<PurchaseItemDistributionEntity> entities) {
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}

