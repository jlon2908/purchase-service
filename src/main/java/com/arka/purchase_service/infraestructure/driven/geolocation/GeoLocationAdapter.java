package com.arka.purchase_service.infraestructure.driven.geolocation;

import com.arka.purchase_service.application.ports.out.GeoLocationPort;
import com.arka.purchase_service.domain.model.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GeoLocationAdapter implements GeoLocationPort {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<GeoLocation> getLocation(String city, String state, String country) {
        String query = String.format("%s, %s, %s", city, state, country);
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("nominatim.openstreetmap.org")
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .build())
                .header("User-Agent", "ArkaInventoryService/1.0 (jonathanlon.66@gmail.com)")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    // Parsear el JSON manualmente para extraer lat/lon
                    // Se espera un array JSON con al menos un objeto
                    try {
                        com.fasterxml.jackson.databind.JsonNode arr = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
                        if (arr.isArray() && arr.size() > 0) {
                            com.fasterxml.jackson.databind.JsonNode obj = arr.get(0);
                            double lat = obj.has("lat") ? obj.get("lat").asDouble() : 0.0;
                            double lon = obj.has("lon") ? obj.get("lon").asDouble() : 0.0;
                            return Mono.just(new GeoLocation(lat, lon));
                        }
                        return Mono.empty();
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}

