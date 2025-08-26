package com.github.carlota.aviation_data_gateway.application.service.airport;

import com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi.AviationDataProvider;
import com.github.carlota.aviation_data_gateway.domain.exception.ResourceNotFoundException;
import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AviationDataProvider provider;

    @Cacheable("airports")
    public Map<String, List<Airport>> getAirportsByIcao(String icaoCode) {
        log.info("Service request to fetch airports by ICAO={}", icaoCode);
        return provider.findAirportsByIcaoCode(icaoCode)
                .orElseThrow(() -> {
                    log.warn("No airports found for ICAO={}", icaoCode);
                    return new ResourceNotFoundException("Airport not found for ICAO=" + icaoCode);
                });
    }

}
