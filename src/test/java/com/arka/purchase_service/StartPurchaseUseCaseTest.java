package com.arka.purchase_service;

import com.arka.purchase_service.application.usecase.StartPurchaseUseCase;
import com.arka.purchase_service.application.ports.out.*;
import com.arka.purchase_service.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StartPurchaseUseCaseTest {
    @MockBean
    private CartQueryPort cartQueryPort;
    @MockBean
    private InventoryQueryPort inventoryQueryPort;
    @MockBean
    private ProductQueryPort productQueryPort;
    @MockBean
    private GeoLocationPort geoLocationPort;
    @MockBean
    private WarehouseQueryPort warehouseQueryPort;
    @MockBean
    private PurchasePersistencePort purchasePersistencePort;
    @MockBean
    private StripeCheckoutPort stripeCheckoutPort;

    @Autowired
    private StartPurchaseUseCase startPurchaseUseCase;

    @Test
    void startPurchase_returnsPurchase_whenCartIsValid() {
        String userId = UUID.randomUUID().toString();
        String jwtToken = "token";
        ShippingAddress address = ShippingAddress.builder().city("City").state("State").country("Country").build();
        CartItem cartItem = CartItem.builder().sku("sku1").quantity(2).build();
        ProductPrice price = ProductPrice.builder().sku("sku1").price(BigDecimal.TEN).currency("USD").build();
        InventoryStock stock = InventoryStock.builder().sku("sku1").available(10).warehouseStock(Map.of("W1", 10)).build();
        GeoLocation geo = GeoLocation.builder().latitude(1.0).longitude(1.0).build();
        Warehouse warehouse = Warehouse.builder().code("W1").latitude(1.0).longitude(1.0).build();
        Purchase purchase = Purchase.builder().id(UUID.randomUUID()).orderCode("ORD-1").clientId(UUID.fromString(userId)).build();

        when(cartQueryPort.getCartItems(anyString(), anyString())).thenReturn(Flux.just(cartItem));
        when(productQueryPort.getProductPriceBySku(anyString())).thenReturn(Mono.just(price));
        when(inventoryQueryPort.getStockBySku(anyString())).thenReturn(Mono.just(stock));
        when(geoLocationPort.getLocation(anyString(), anyString(), anyString())).thenReturn(Mono.just(geo));
        when(warehouseQueryPort.getAllWarehouses()).thenReturn(Flux.just(warehouse));
        when(purchasePersistencePort.savePurchase(any())).thenReturn(Mono.just(purchase));
        when(stripeCheckoutPort.createCheckoutSession(anyList(), anyString())).thenReturn(Mono.just("http://stripe-session-url"));

        Purchase result = startPurchaseUseCase.startPurchase(address, userId, jwtToken).block();
        assertNotNull(result);
        assertEquals("ORD-1", result.getOrderCode());
    }

    @Test
    void startPurchase_throwsException_whenCartIsEmpty() {
        when(cartQueryPort.getCartItems(anyString(), anyString())).thenReturn(Flux.empty());
        Mono<Purchase> result = startPurchaseUseCase.startPurchase(new ShippingAddress(), UUID.randomUUID().toString(), "token");
        assertThrows(IllegalStateException.class, result::block);
    }
}
