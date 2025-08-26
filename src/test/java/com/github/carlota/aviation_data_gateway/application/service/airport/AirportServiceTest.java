package com.github.carlota.aviation_data_gateway.application.service.airport;

import com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi.AviationDataProvider;
import com.github.carlota.aviation_data_gateway.domain.exception.ResourceNotFoundException;
import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AirportService.
 * These tests verify the business logic in isolation, mocking the data provider dependency.
 */
@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AviationDataProvider provider;

    @InjectMocks
    private AirportService airportService;

    @Test
    void getAirportsByIcao_shouldReturnAirportMap_whenProviderFindsData() {
        // Arrange
        String icaoCode = "SBGR";
        Airport airport = Airport.builder().icaoIdent(icaoCode).build();
        Map<String, List<Airport>> expectedResponse = Collections.singletonMap(icaoCode, List.of(airport));

        // Mock the provider to return data
        when(provider.findAirportsByIcaoCode(icaoCode)).thenReturn(Optional.of(expectedResponse));

        // Act
        Map<String, List<Airport>> actualResponse = airportService.getAirportsByIcao(icaoCode);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        // Verify that the provider was called exactly once
        verify(provider, times(1)).findAirportsByIcaoCode(icaoCode);
    }

    @Test
    void getAirportsByIcao_shouldThrowResourceNotFoundException_whenProviderReturnsEmpty() {
        // Arrange
        String icaoCode = "XXXX";

        // Mock the provider to return an empty optional
        when(provider.findAirportsByIcaoCode(icaoCode)).thenReturn(Optional.empty());

        // Act & Assert
        // Verify that the expected exception is thrown
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            airportService.getAirportsByIcao(icaoCode);
        });

        // Optionally, assert the exception message
        assertEquals("Airport not found for ICAO=" + icaoCode, exception.getMessage());

        // Verify that the provider was still called
        verify(provider, times(1)).findAirportsByIcaoCode(icaoCode);
    }
}
