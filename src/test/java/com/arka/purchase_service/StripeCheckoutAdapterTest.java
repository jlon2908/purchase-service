package com.arka.purchase_service;

import com.arka.purchase_service.application.ports.out.StripeCheckoutPort;
import com.arka.purchase_service.domain.model.PurchaseItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class StripeCheckoutAdapterTest {
    @MockBean
    private StripeCheckoutPort stripeCheckoutPort;

    @Test
    void createCheckoutSession_returnsUrl() {
        PurchaseItem item = PurchaseItem.builder()
                .sku("sku1")
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .build();
        String orderCode = "ORD-123";
        String url = "https://checkout.stripe.com/pay/test-url";
        when(stripeCheckoutPort.createCheckoutSession(List.of(item), orderCode)).thenReturn(Mono.just(url));

        StepVerifier.create(stripeCheckoutPort.createCheckoutSession(List.of(item), orderCode))
                .expectNext(url)
                .verifyComplete();
    }
}


