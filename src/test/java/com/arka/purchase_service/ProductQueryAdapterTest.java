package com.arka.purchase_service;

import com.arka.purchase_service.application.ports.out.ProductQueryPort;
import com.arka.purchase_service.domain.model.ProductPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductQueryAdapterTest {
    @MockBean
    private ProductQueryPort productQueryPort;

    @Test
    void getProductPriceBySku_returnsProductPrice() {
        String sku = "sku1";
        ProductPrice price = ProductPrice.builder()
                .sku(sku)
                .price(BigDecimal.TEN)
                .currency("USD")
                .build();
        when(productQueryPort.getProductPriceBySku(sku)).thenReturn(Mono.just(price));

        StepVerifier.create(productQueryPort.getProductPriceBySku(sku))
                .expectNextMatches(p -> p.getSku().equals(sku) && p.getPrice().compareTo(BigDecimal.TEN) == 0)
                .verifyComplete();
    }
}


