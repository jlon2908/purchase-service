package com.arka.purchase_service.infraestructure.driven.persistence.adapter;

import com.arka.purchase_service.application.ports.out.PurchasePersistencePort;
import com.arka.purchase_service.domain.model.*;
import com.arka.purchase_service.infraestructure.driven.persistence.entity.*;
import com.arka.purchase_service.infraestructure.driven.persistence.mapper.PurchasePersistenceMapper;
import com.arka.purchase_service.infraestructure.driven.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PurchasePersistenceAdapter implements PurchasePersistencePort {
    private final PurchaseRepository purchaseRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseItemDistributionRepository purchaseItemDistributionRepository;
    private final PurchasePersistenceMapper mapper;

    @Override
    public Mono<Purchase> savePurchase(Purchase purchase) {
        System.out.println("[PurchasePersistenceAdapter] Guardando purchase: " + purchase);
        PurchaseEntity purchaseEntity = mapper.toEntity(purchase);
        // Usar el método upsertPurchase personalizado
        return purchaseRepository.upsertPurchase(
                purchaseEntity.getId(),
                purchaseEntity.getOrderCode(),
                purchaseEntity.getClientId(),
                purchaseEntity.getWarehousePickup(),
                purchaseEntity.getPurchaseDate(),
                purchaseEntity.getStatus(),
                purchaseEntity.getTotalAmount()
        )
        .then(Mono.defer(() -> {
            // Guardar dirección de envío
            ShippingAddressEntity addressEntity = mapper.toEntity(purchase.getShippingAddress(), purchaseEntity.getId());
            Mono<Void> addressMono = shippingAddressRepository.upsertShippingAddress(
                    addressEntity.getId(),
                    addressEntity.getPurchaseId(),
                    addressEntity.getStreet(),
                    addressEntity.getCity(),
                    addressEntity.getState(),
                    addressEntity.getCountry(),
                    addressEntity.getNotes()
            ).doOnSuccess(v -> System.out.println("[PurchasePersistenceAdapter] ShippingAddress guardada: " + addressEntity.getId()));

            List<PurchaseItem> items = purchase.getItems();
            List<PurchaseItemEntity> itemEntities = mapper.toItemEntities(items, purchaseEntity.getId());
            Flux<Void> savedItemsFlux = Flux.fromIterable(itemEntities)
                .flatMap(itemEntity -> purchaseItemRepository.upsertPurchaseItem(
                        itemEntity.getId(),
                        itemEntity.getPurchaseId(),
                        itemEntity.getSku(),
                        itemEntity.getQuantity(),
                        itemEntity.getUnitPrice(),
                        itemEntity.getSubtotal()
                ).doOnSuccess(v -> System.out.println("[PurchasePersistenceAdapter] PurchaseItem guardado: " + itemEntity.getId())));

            // Guardar distribuciones solo después de guardar los ítems
            Flux<Void> savedDistributionsFlux = savedItemsFlux.thenMany(
                Flux.fromIterable(itemEntities)
                    .flatMap(savedItem -> {
                        PurchaseItem item = items.stream().filter(i -> i.getSku().equals(savedItem.getSku())).findFirst().orElse(null);
                        if (item == null || item.getDistributions() == null) return Flux.empty();
                        List<PurchaseItemDistributionEntity> distEntities = mapper.toDistributionEntities(item.getDistributions(), savedItem.getId());
                        return Flux.fromIterable(distEntities)
                            .flatMap(distEntity -> purchaseItemDistributionRepository.upsertPurchaseItemDistribution(
                                    distEntity.getId(),
                                    distEntity.getPurchaseItemId(),
                                    distEntity.getWarehouseCode(),
                                    distEntity.getQuantity()
                            ).doOnSuccess(v -> System.out.println("[PurchasePersistenceAdapter] PurchaseItemDistribution guardada: " + distEntity.getId())));
                    })
            );

            return Mono.when(addressMono, savedItemsFlux.then(), savedDistributionsFlux.then())
                    .doOnError(e -> {
                        System.out.println("[PurchasePersistenceAdapter] Error guardando datos: " + e.getMessage());
                        e.printStackTrace();
                    })
                    .thenReturn(purchase);
        }));
    }
}
