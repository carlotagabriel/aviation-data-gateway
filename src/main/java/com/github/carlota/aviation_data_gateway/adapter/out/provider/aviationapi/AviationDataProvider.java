package com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi;

import com.github.carlota.aviation_data_gateway.domain.model.Airport;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AviationDataProvider {
    Optional<Map<String, List<Airport>>> findAirportsByIcaoCode(String icaoCode);
}
