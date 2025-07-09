package com.arka.purchase_service;

import com.arka.purchase_service.application.usecase.CompletePurchaseUseCase;
import com.arka.purchase_service.application.ports.out.CartCommandPort;
import com.arka.purchase_service.application.ports.out.OrderEventPort;
import com.arka.purchase_service.application.ports.out.PurchaseQueryPort;
import com.arka.purchase_service.application.ports.out.PurchaseStatusUpdatePort;
import com.arka.purchase_service.domain.model.Purchase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import java.util.UUID;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CompletePurchaseUseCaseTest {
    @MockBean
    private PurchaseStatusUpdatePort purchaseStatusUpdatePort;
    @MockBean
    private PurchaseQueryPort purchaseQueryPort;
    @MockBean
    private CartCommandPort cartCommandPort;
    @MockBean
    private OrderEventPort orderEventPort;

    @Autowired
    private CompletePurchaseUseCase completePurchaseUseCase;

    @Test
    void completePurchase_updatesStatusAndClearsCartAndPublishesEvent() {
        String orderCode = "ORD-123";
        Purchase purchase = Purchase.builder().id(UUID.randomUUID()).orderCode(orderCode).clientId(UUID.randomUUID()).build();
        when(purchaseStatusUpdatePort.updateStatusByOrderCode(eq(orderCode), eq("PAID"))).thenReturn(Mono.empty());
        when(purchaseQueryPort.findByOrderCode(orderCode)).thenReturn(Mono.just(purchase));
        when(cartCommandPort.clearCart(anyString(), any())).thenReturn(Mono.empty());
        when(orderEventPort.publishOrderPlacedEvent(any())).thenReturn(Mono.empty());

        completePurchaseUseCase.completePurchase(orderCode).block();
        verify(purchaseStatusUpdatePort).updateStatusByOrderCode(orderCode, "PAID");
        verify(purchaseQueryPort).findByOrderCode(orderCode);
        verify(cartCommandPort).clearCart(anyString(), any());
        verify(orderEventPort).publishOrderPlacedEvent(purchase);
    }
}

