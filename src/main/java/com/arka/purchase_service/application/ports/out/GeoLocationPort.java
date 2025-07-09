package com.arka.purchase_service.application.ports.out;

import com.arka.purchase_service.domain.model.GeoLocation;
import reactor.core.publisher.Mono;

public interface GeoLocationPort {
    Mono<GeoLocation> getLocation(String city, String state, String country);
}

