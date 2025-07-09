package com.arka.purchase_service;

import com.arka.purchase_service.application.ports.out.GeoLocationPort;
import com.arka.purchase_service.domain.model.GeoLocation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class GeoLocationAdapterTest {
    @MockBean
    private GeoLocationPort geoLocationPort;

    @Test
    void getLocation_returnsGeoLocation() {
        String city = "Ciudad";
        String state = "Estado";
        String country = "Pais";
        GeoLocation geo = GeoLocation.builder().latitude(10.0).longitude(20.0).build();
        when(geoLocationPort.getLocation(city, state, country)).thenReturn(Mono.just(geo));

        StepVerifier.create(geoLocationPort.getLocation(city, state, country))
                .expectNextMatches(g -> g.getLatitude() == 10.0 && g.getLongitude() == 20.0)
                .verifyComplete();
    }
}


