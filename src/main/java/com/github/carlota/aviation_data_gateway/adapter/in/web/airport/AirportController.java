package com.github.carlota.aviation_data_gateway.adapter.in.web.airport;

import com.github.carlota.aviation_data_gateway.adapter.in.web.airport.dto.AirportMapper;
import com.github.carlota.aviation_data_gateway.adapter.in.web.airport.dto.AirportResponseDto;
import com.github.carlota.aviation_data_gateway.application.service.airport.AirportService;
import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
@Validated
public class AirportController {

    private final AirportService airportService;
    private final AirportMapper airportMapper;

    @GetMapping("/{icaoCode}")
    public Map<String, List<AirportResponseDto>> getAirports(@PathVariable String icaoCode) {
        log.info("HTTP GET /api/v1/airports/{} invoked", icaoCode);
        Map<String, List<Airport>> airports = airportService.getAirportsByIcao(icaoCode);
        int count = airports.values().stream().mapToInt(List::size).sum();
        log.info("Returning {} airport record(s) for ICAO={}", count, icaoCode);
        return airportMapper.toDtoMap(airports);
    }

}
