package com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi;

import com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi.dto.AirportProviderMapper;
import com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi.dto.AviationApiDto;
import com.github.carlota.aviation_data_gateway.domain.exception.DataProviderException;
import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AviationApiComProvider implements AviationDataProvider {

    private final WebClient.Builder webClientBuilder;
    private final AirportProviderMapper airportProviderMapper;
    private final MeterRegistry meterRegistry; // Adicionado de volta

    @Value("${aviation.api.base-url}")
    private String baseUrl;

    @Value("${aviation.api.timeout.read-ms:3000}")
    private long readTimeoutMs;

    @Override
    @CircuitBreaker(name = "aviationApi", fallbackMethod = "fallbackFindByIcao")
    @Retry(name = "aviationApi")
    public Optional<Map<String, List<Airport>>> findAirportsByIcaoCode(String icaoCode) {
        log.debug("Attempting to fetch airports from provider for ICAO={}", icaoCode);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();
            ParameterizedTypeReference<Map<String, List<AviationApiDto>>> responseType = new ParameterizedTypeReference<>() {};

            Map<String, List<AviationApiDto>> responseMap = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/airports").queryParam("apt", icaoCode).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(Duration.ofMillis(readTimeoutMs))
                    .block();

            if (responseMap == null || responseMap.isEmpty() ||
                    responseMap.values().stream().allMatch(List::isEmpty)) {
                return Optional.empty();
            }

            return Optional.ofNullable(responseMap)
                    .map(airportProviderMapper::toDomainMap);
        } finally {
            sample.stop(Timer.builder("aviation.api.latency")
                    .tag("operation", "findAirportByIcaoCode")
                    .register(meterRegistry));
        }
    }

    public Optional<Map<String, List<Airport>>> fallbackFindByIcao(String icaoCode, Throwable cause) {
        log.error("Fallback triggered for ICAO={} due to: {}", icaoCode, cause.getMessage());
//        throw new DataProviderException("Aviation provider unavailable while fetching ICAO=" + icaoCode, cause);
        return Optional.empty();
    }

}
