package com.arka.purchase_service.infraestructure.driven.persistence.repository;

import com.arka.purchase_service.infraestructure.driven.persistence.entity.ShippingAddressEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShippingAddressRepository extends ReactiveCrudRepository<ShippingAddressEntity, UUID> {
    @Query("INSERT INTO shipping_address (id, purchase_id, street, city, state, country, notes) " +
           "VALUES (:id, :purchaseId, :street, :city, :state, :country, :notes) " +
           "ON CONFLICT (id) DO UPDATE SET " +
           "purchase_id = EXCLUDED.purchase_id, street = EXCLUDED.street, city = EXCLUDED.city, state = EXCLUDED.state, country = EXCLUDED.country, notes = EXCLUDED.notes")
    Mono<Void> upsertShippingAddress(UUID id, UUID purchaseId, String street, String city, String state, String country, String notes);
}
