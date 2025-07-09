package com.arka.purchase_service.application.usecase;

import com.arka.purchase_service.application.ports.in.StartPurchasePort;
import com.arka.purchase_service.application.ports.out.*;
import com.arka.purchase_service.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class StartPurchaseUseCase implements StartPurchasePort {
    private final CartQueryPort cartQueryPort;
    private final InventoryQueryPort inventoryQueryPort;
    private final ProductQueryPort productQueryPort;
    private final GeoLocationPort geoLocationPort;
    private final WarehouseQueryPort warehouseQueryPort;
    private final PurchasePersistencePort purchasePersistencePort;
    private final StripeCheckoutPort stripeCheckoutPort;

    @Override
    public Mono<Purchase> startPurchase(ShippingAddress shippingAddress, String userId, String jwtToken) {
        // Paso 1: Obtener ítems del carrito
        return cartQueryPort.getCartItems(userId, jwtToken)
                .collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) {
                        return Mono.error(new IllegalStateException("El carrito está vacío"));
                    }
                    // Paso 2: Por cada SKU, validar stock y obtener precio
                    return Flux.fromIterable(cartItems)
                            .flatMap(cartItem ->
                                    Mono.zip(
                                            inventoryQueryPort.getStockBySku(cartItem.getSku()),
                                            productQueryPort.getProductPriceBySku(cartItem.getSku()),
                                            (stock, price) -> new Object[]{cartItem, stock, price}
                                    )
                            )
                            .collectList()
                            .flatMap(itemsWithDetails -> {
                                // Paso 3: Calcular total y obtener moneda
                                BigDecimal total = itemsWithDetails.stream()
                                        .map(obj -> {
                                            CartItem cartItem = (CartItem) obj[0];
                                            ProductPrice price = (ProductPrice) obj[2];
                                            return price.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                                        })
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                String currency = ((ProductPrice) itemsWithDetails.get(0)[2]).getCurrency();

                                // Paso 4: Obtener coordenadas de la dirección
                                return geoLocationPort.getLocation(
                                        shippingAddress.getCity(),
                                        shippingAddress.getState(),
                                        shippingAddress.getCountry()
                                ).doOnNext(location -> {
                                    System.out.println("[StartPurchaseUseCase] Latitud obtenida: " + location.getLatitude());
                                    System.out.println("[StartPurchaseUseCase] Longitud obtenida: " + location.getLongitude());
                                })

                                        .flatMap(geoLocation ->
                                        // Paso 5: Obtener bodegas
                                        warehouseQueryPort.getAllWarehouses().collectList().flatMap(warehouses -> {
                                            System.out.println("Cantidad de bodegas obtenidas: " + warehouses.size());
                                            // Paso 6: Distribuir ítems entre bodegas (lógica real)
                                            List<PurchaseItem> purchaseItems = new ArrayList<>();
                                            for (Object[] obj : itemsWithDetails) {
                                                CartItem cartItem = (CartItem) obj[0];
                                                InventoryStock stock = (InventoryStock) obj[1];
                                                ProductPrice price = (ProductPrice) obj[2];
                                                int[] quantityToAssign = {cartItem.getQuantity()};
                                                List<PurchaseItemDistribution> distributions = new ArrayList<>();

                                                // Ordenar bodegas por distancia a la ubicación del cliente
                                                warehouses.stream()
                                                        .filter(w -> w.getLatitude() != 0 && w.getLongitude() != 0)
                                                        .sorted(Comparator.comparingDouble(w -> distance(geoLocation.getLatitude(), geoLocation.getLongitude(), w.getLatitude(), w.getLongitude())))
                                                        .forEach(warehouse -> {
                                                            int available = stock.getWarehouseStock().getOrDefault(warehouse.getCode(), 0);
                                                            if (quantityToAssign[0] > 0 && available > 0) {
                                                                int assignQty = Math.min(quantityToAssign[0], available);
                                                                distributions.add(PurchaseItemDistribution.builder()
                                                                        .id(UUID.randomUUID())
                                                                        .warehouseCode(warehouse.getCode())
                                                                        .quantity(assignQty)
                                                                        .build());
                                                                quantityToAssign[0] -= assignQty;
                                                            }
                                                        });
                                                // NO asignar a bodegas sin stock, si queda por asignar, simplemente no se asigna
                                                purchaseItems.add(PurchaseItem.builder()
                                                        .id(UUID.randomUUID())
                                                        .sku(cartItem.getSku())
                                                        .quantity(cartItem.getQuantity())
                                                        .unitPrice(price.getPrice())
                                                        .subtotal(price.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                                                        .distributions(distributions)
                                                        .build());
                                            }
                                            // Seleccionar la bodega pickup más cercana (sin importar stock)
                                            Warehouse selectedWarehouse = warehouses.stream()
                                                    .filter(w -> w.getLatitude() != 0 && w.getLongitude() != 0)
                                                    .min(Comparator.comparingDouble(w -> distance(geoLocation.getLatitude(), geoLocation.getLongitude(), w.getLatitude(), w.getLongitude())))
                                                    .orElseThrow(() -> new IllegalStateException("No hay bodegas con coordenadas válidas"));
                                            // Paso 7: Registrar en la base de datos
                                            ShippingAddress addressWithId = ShippingAddress.builder()
                                                    .id(UUID.randomUUID())
                                                    .street(shippingAddress.getStreet())
                                                    .city(shippingAddress.getCity())
                                                    .state(shippingAddress.getState())
                                                    .country(shippingAddress.getCountry())
                                                    .notes(shippingAddress.getNotes())
                                                    .build();
                                            Purchase purchase = Purchase.builder()
                                                    .id(UUID.randomUUID())
                                                    .orderCode("ORD-" + System.currentTimeMillis())
                                                    .clientId(UUID.fromString(userId))
                                                    .warehousePickup(selectedWarehouse.getCode())
                                                    .purchaseDate(LocalDateTime.now())
                                                    .status("PENDING")
                                                    .totalAmount(total)
                                                    .shippingAddress(addressWithId)
                                                    .items(purchaseItems)
                                                    .build();
                                            return purchasePersistencePort.savePurchase(purchase)
                                                    .flatMap(saved -> {
                                                        System.out.println("Antes de llamar a Stripe");
                                                        return stripeCheckoutPort.createCheckoutSession(purchaseItems, purchase.getOrderCode())
                                                                .doOnNext(url -> System.out.println("Stripe Checkout URL generado: " + url))
                                                                .doOnError(e -> {
                                                                    System.out.println("Error al crear sesión de Stripe: " + e.getMessage());
                                                                    e.printStackTrace();
                                                                })
                                                                .map(url -> {
                                                                    saved.setStripeCheckoutUrl(url);
                                                                    return saved;
                                                                });
                                                    });
                                        })
                                );
                            });
                })
                .doOnError(e -> {
                    System.out.println("[StartPurchaseUseCase] Error global en el flujo: " + e.getMessage());
                    e.printStackTrace();
                });
    }

    // Haversine formula para calcular distancia entre dos puntos geográficos
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
