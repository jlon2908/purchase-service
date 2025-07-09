package com.arka.purchase_service.application.usecase;

import com.arka.purchase_service.application.ports.in.CompletePurchasePort;
import com.arka.purchase_service.application.ports.out.CartCommandPort;
import com.arka.purchase_service.application.ports.out.OrderEventPort;
import com.arka.purchase_service.application.ports.out.PurchaseQueryPort;
import com.arka.purchase_service.application.ports.out.PurchaseStatusUpdatePort;
import com.arka.purchase_service.domain.model.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class CompletePurchaseUseCase implements CompletePurchasePort {
    private final PurchaseStatusUpdatePort purchaseStatusUpdatePort;
    private final PurchaseQueryPort purchaseQueryPort;
    private final CartCommandPort cartCommandPort;
    private final OrderEventPort orderEventPort;
    private static final Logger logger = LoggerFactory.getLogger(CompletePurchaseUseCase.class);


    @Override
    public Mono<Void> completePurchase(String orderCode) {
        return purchaseStatusUpdatePort.updateStatusByOrderCode(orderCode, "PAID")
                .then(purchaseQueryPort.findByOrderCode(orderCode))
                .flatMap(purchase -> {
                    String userId = purchase.getClientId().toString();
                    // Limpiar carrito
                    return cartCommandPort.clearCart(userId, null)
                            .doOnSuccess(v -> logger.info("📦 Purchase ready to send as order.placed event: \n{}", purchase))

                            // Publicar evento order.placed a inventario
                        .then(orderEventPort.publishOrderPlacedEvent(purchase));
                });
    }
}
