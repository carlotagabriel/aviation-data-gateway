package com.github.carlota.aviation_data_gateway;

import com.github.carlota.aviation_data_gateway.adapter.in.web.airport.dto.AirportResponseDto;
import com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi.AviationDataProvider;
import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A light integration smoke test for the application.
 * This test starts the full Spring Boot application on a random port and uses
 * a TestRestTemplate to make a real HTTP call to the endpoint.
 * The external dependency (AviationDataProvider) is replaced with a stub using @TestConfiguration.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationSmokeTest {

    /**
     * Inner configuration class to provide a stub implementation of the AviationDataProvider.
     * This bean will replace the production AviationApiComProvider bean during this test.
     *
     */
    @TestConfiguration
    static class StubProviderConfig {
        @Bean
        @Primary
        AviationDataProvider aviationDataProvider() {
            return icao -> {
                // Return a predictable, stubbed response
                Airport stubbedAirport = Airport.builder()
                        .icaoIdent(icao)
                        .facilityName("Stubbed Airport")
                        .city("Test City")
                        .build();
                Map<String, List<Airport>> responseMap = Collections.singletonMap(icao, List.of(stubbedAirport));
                return Optional.of(responseMap);
            };
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void endpointRespondsSuccessfully() {
        // Arrange
        String testIcaoCode = "ATL";
        String url = "/api/v1/airports/{icaoCode}"; //

        // Define the expected complex response type for the RestTemplate
        ParameterizedTypeReference<Map<String, List<AirportResponseDto>>> responseType =
                new ParameterizedTypeReference<>() {};

        // Act
        ResponseEntity<Map<String, List<AirportResponseDto>>> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, null, responseType, testIcaoCode);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map<String, List<AirportResponseDto>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey(testIcaoCode));

        List<AirportResponseDto> airportList = responseBody.get(testIcaoCode);
        assertFalse(airportList.isEmpty());

        AirportResponseDto airportDto = airportList.get(0); //
        assertEquals(testIcaoCode, airportDto.getIcaoIdent());
        assertEquals("Stubbed Airport", airportDto.getFacilityName());
        assertEquals("Test City", airportDto.getCity());
    }
}
