package com.arka.purchase_service.infraestructure.driven.persistence.adapter;

import com.arka.purchase_service.application.ports.out.PurchaseQueryPort;
import com.arka.purchase_service.application.usecase.CompletePurchaseUseCase;
import com.arka.purchase_service.domain.model.Purchase;
import com.arka.purchase_service.domain.model.PurchaseItem;
import com.arka.purchase_service.domain.model.PurchaseItemDistribution;
import com.arka.purchase_service.domain.model.ShippingAddress;
import com.arka.purchase_service.infraestructure.driven.persistence.entity.PurchaseEntity;
import com.arka.purchase_service.infraestructure.driven.persistence.entity.PurchaseItemDistributionEntity;
import com.arka.purchase_service.infraestructure.driven.persistence.entity.PurchaseItemEntity;
import com.arka.purchase_service.infraestructure.driven.persistence.entity.ShippingAddressEntity;
import com.arka.purchase_service.infraestructure.driven.persistence.mapper.PurchasePersistenceMapper;
import com.arka.purchase_service.infraestructure.driven.persistence.repository.PurchaseItemDistributionRepository;
import com.arka.purchase_service.infraestructure.driven.persistence.repository.PurchaseItemRepository;
import com.arka.purchase_service.infraestructure.driven.persistence.repository.ShippingAddressRepository;
import com.arka.purchase_service.infraestructure.driven.persistence.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class PurchaseQueryAdapter implements PurchaseQueryPort {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseItemDistributionRepository purchaseItemDistributionRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final PurchasePersistenceMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(CompletePurchaseUseCase.class);

    @Override
    public Mono<Purchase> findByOrderCode(String orderCode) {
        return purchaseRepository.findByOrderCode(orderCode)
                .flatMap(entity -> {
                    Flux<PurchaseItemEntity> itemEntitiesFlux = purchaseItemRepository.findAll()
                        .filter(item -> item.getPurchaseId().equals(entity.getId()));
                    return itemEntitiesFlux.collectList().flatMap(itemEntities -> {
                        if (itemEntities.isEmpty()) {
                            // No items, solo datos básicos
                            return Mono.just(mapper.toDomain(entity, null, Collections.emptyList()));
                        }
                        // Para cada item, buscar sus distribuciones
                        List<Mono<List<PurchaseItemDistribution>>> distMonos = itemEntities.stream()
                                .map(itemEntity -> purchaseItemDistributionRepository.findAll()
                                        .filter(dist -> dist.getPurchaseItemId().equals(itemEntity.getId()))
                                        .collectList()
                                        .map(mapper::toDistributionDomains)
                                ).collect(Collectors.toList());
                        return Flux.mergeSequential(distMonos)
                                .collectList()
                                .flatMap(distsList -> {
                                    List<PurchaseItem> items = mapper.toItemDomains(itemEntities, distsList);
                                    // No buscar ni mapear ShippingAddress
                                    return Mono.just(mapper.toDomain(entity, null, items));
                                });
                    });
                });
    }

    private Purchase toDomain(PurchaseEntity entity) {
        return Purchase.builder()
                .id(entity.getId())
                .orderCode(entity.getOrderCode())
                .clientId(entity.getClientId())
                .warehousePickup(entity.getWarehousePickup())
                .purchaseDate(entity.getPurchaseDate())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .build();
    }
}
