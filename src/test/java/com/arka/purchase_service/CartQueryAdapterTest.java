package com.arka.purchase_service;


import com.arka.purchase_service.application.ports.out.CartQueryPort;
import com.arka.purchase_service.domain.model.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.util.List;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CartQueryAdapterTest {
    @MockBean
    private CartQueryPort cartQueryPort;

    @Test
    void getCartItems_returnsCartItems() {
        String userId = "user-123";
        String jwtToken = "token";
        CartItem item1 = CartItem.builder().sku("sku1").quantity(2).build();
        CartItem item2 = CartItem.builder().sku("sku2").quantity(1).build();
        when(cartQueryPort.getCartItems(userId, jwtToken)).thenReturn(Flux.fromIterable(List.of(item1, item2)));

        StepVerifier.create(cartQueryPort.getCartItems(userId, jwtToken).collectList())
                .expectNextMatches(list -> list.size() == 2 && list.get(0).getSku().equals("sku1") && list.get(1).getSku().equals("sku2"))
                .verifyComplete();
    }
}


