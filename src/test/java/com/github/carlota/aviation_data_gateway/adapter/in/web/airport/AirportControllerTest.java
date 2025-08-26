package com.github.carlota.aviation_data_gateway.adapter.in.web.airport;

import com.github.carlota.aviation_data_gateway.adapter.in.web.airport.dto.AirportMapper;
import com.github.carlota.aviation_data_gateway.adapter.in.web.airport.dto.AirportResponseDto;
import com.github.carlota.aviation_data_gateway.application.service.airport.AirportService;
import com.github.carlota.aviation_data_gateway.domain.exception.ResourceNotFoundException;
import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirportController.class)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirportService airportService;

    @MockBean
    private AirportMapper airportMapper;

    @Test
    void getAirports_shouldReturnOkAndData_whenIcaoCodeIsValid() throws Exception {
        // Arrange
        String icaoCode = "SBGR";
        Airport airport = Airport.builder().icaoIdent(icaoCode).facilityName("Guarulhos International").build();
        AirportResponseDto responseDto = AirportResponseDto.builder().icaoIdent(icaoCode).facilityName("Guarulhos International").build();

        Map<String, List<Airport>> serviceResult = Collections.singletonMap(icaoCode, List.of(airport));
        Map<String, List<AirportResponseDto>> mapperResult = Collections.singletonMap(icaoCode, List.of(responseDto));

        when(airportService.getAirportsByIcao(icaoCode)).thenReturn(serviceResult);
        when(airportMapper.toDtoMap(any())).thenReturn(mapperResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/airports/{icaoCode}", icaoCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$." + icaoCode).isArray())
                .andExpect(jsonPath("$." + icaoCode + "[0].icao_ident").value(icaoCode))
                .andExpect(jsonPath("$." + icaoCode + "[0].facility_name").value("Guarulhos International"));
    }
}
