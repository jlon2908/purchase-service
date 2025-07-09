package com.arka.purchase_service.infraestructure.driver.rest;

import com.arka.purchase_service.application.ports.in.StartPurchasePort;
import com.arka.purchase_service.domain.model.Purchase;
import com.arka.purchase_service.domain.model.ShippingAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final StartPurchasePort startPurchasePort;

    @PostMapping("/start")
    public Mono<Purchase> startPurchase(@RequestBody ShippingAddress shippingAddress,
                                        @AuthenticationPrincipal Jwt jwt,
                                        @RequestHeader("Authorization") String authorizationHeader) {
        String userId = jwt.getClaimAsString("userId"); // Cambiado para usar el UUID del claim userId
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        return startPurchasePort.startPurchase(shippingAddress, userId, jwtToken);
    }
}
