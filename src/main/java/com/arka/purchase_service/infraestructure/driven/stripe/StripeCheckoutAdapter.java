package com.arka.purchase_service.infraestructure.driven.stripe;

import com.arka.purchase_service.application.ports.out.StripeCheckoutPort;
import com.arka.purchase_service.domain.model.PurchaseItem;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StripeCheckoutAdapter implements StripeCheckoutPort {
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;


    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public Mono<String> createCheckoutSession(List<PurchaseItem> items, String orderCode) {
        return Mono.fromCallable(() -> {
            List<SessionCreateParams.LineItem> lineItems = items.stream().map(item ->
                SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd") // Puedes cambiar la moneda si lo necesitas
                            .setUnitAmount(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(100)).longValue()) // Stripe espera centavos
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getSku())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            ).collect(Collectors.toList());

            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://example.com/cancel")
                .putMetadata("orderCode", orderCode)
                .addAllLineItem(lineItems)
                .build();

            Session session = Session.create(params);
            return session.getUrl();
        });
    }
}

