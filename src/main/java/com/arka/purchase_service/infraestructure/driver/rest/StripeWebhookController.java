package com.arka.purchase_service.infraestructure.driver.rest;

import com.arka.purchase_service.application.ports.in.CompletePurchasePort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/purchases/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {
    private final CompletePurchasePort completePurchasePort;

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    @PostMapping
    public Mono<ResponseEntity<String>> handleStripeWebhook(@RequestBody String payload,
                                                           @RequestHeader("Stripe-Signature") String sigHeader) {
        System.out.println("[StripeWebhookController] Webhook recibido de Stripe");
        System.out.println("[StripeWebhookController] Payload recibido: " + payload);
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("[StripeWebhookController] Firma de Stripe válida. Tipo de evento: " + (event != null ? event.getType() : "null"));
        } catch (Exception e) {
            System.out.println("[StripeWebhookController] Error al validar firma o parsear evento: " + e.getMessage());
            e.printStackTrace();
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida o error de parseo: " + e.getMessage()));
        }

        if (event != null && "checkout.session.completed".equals(event.getType())) {
            System.out.println("[StripeWebhookController] Evento checkout.session.completed recibido");
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(payload);
                JsonNode metadataNode = root.path("data").path("object").path("metadata");
                String orderCode = metadataNode.path("orderCode").asText(null);
                System.out.println("[StripeWebhookController] orderCode extraído manualmente: " + orderCode);
                if (orderCode == null) {
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("orderCode no encontrado en metadata"));
                }
                return completePurchasePort.completePurchase(orderCode)
                        .doOnSuccess(v -> System.out.println("[StripeWebhookController] Estado actualizado a PAID y carrito limpiado para orderCode: " + orderCode))
                        .thenReturn(ResponseEntity.ok("Estado actualizado a PAID y carrito limpiado"));
            } catch (Exception e) {
                System.out.println("[StripeWebhookController] Error parseando el payload manualmente: " + e.getMessage());
                e.printStackTrace();
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parseando el payload: " + e.getMessage()));
            }
        }
        System.out.println("[StripeWebhookController] Evento ignorado: " + (event != null ? event.getType() : "null"));
        return Mono.just(ResponseEntity.ok("Evento ignorado"));
    }
}
