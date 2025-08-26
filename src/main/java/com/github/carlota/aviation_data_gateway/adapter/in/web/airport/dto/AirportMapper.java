package com.github.carlota.aviation_data_gateway.adapter.in.web.airport.dto;

import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps domain objects to DTOs for the web layer.
 */
@Slf4j
@Component
public class AirportMapper {

    public Map<String, List<AirportResponseDto>> toDtoMap(Map<String, List<Airport>> domainMap) {
        if (domainMap == null || domainMap.isEmpty()) {
            log.warn("Attempted to map empty or null airport map to DTOs");
            return Collections.emptyMap();
        }

        String key = domainMap.keySet().iterator().next();
        List<Airport> domainList = domainMap.get(key);
        log.debug("Mapping {} airport(s) under key '{}' to DTOs", domainList != null ? domainList.size() : 0, key);

        List<AirportResponseDto> dtoList = domainList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return Collections.singletonMap(key, dtoList);
    }

    private AirportResponseDto toDto(Airport airport) {
        return AirportResponseDto.builder()
                .siteNumber(airport.getSiteNumber())
                .type(airport.getType())
                .facilityName(airport.getFacilityName())
                .faaIdent(airport.getFaaIdent())
                .icaoIdent(airport.getIcaoIdent())
                .region(airport.getRegion())
                .districtOffice(airport.getDistrictOffice())
                .state(airport.getState())
                .stateFull(airport.getStateFull())
                .county(airport.getCounty())
                .city(airport.getCity())
                .ownership(airport.getOwnership())
                .use(airport.getUse())
                .manager(airport.getManager())
                .managerPhone(airport.getManagerPhone())
                .latitude(airport.getLatitude())
                .latitudeSec(airport.getLatitudeSec())
                .longitude(airport.getLongitude())
                .longitudeSec(airport.getLongitudeSec())
                .elevation(airport.getElevation())
                .magneticVariation(airport.getMagneticVariation())
                .tpa(airport.getTpa())
                .vfrSectional(airport.getVfrSectional())
                .boundaryArtcc(airport.getBoundaryArtcc())
                .boundaryArtccName(airport.getBoundaryArtccName())
                .responsibleArtcc(airport.getResponsibleArtcc())
                .responsibleArtccName(airport.getResponsibleArtccName())
                .fssPhoneNumber(airport.getFssPhoneNumber())
                .fssPhoneNumerTollfree(airport.getFssPhoneNumerTollfree())
                .notamFacilityIdent(airport.getNotamFacilityIdent())
                .status(airport.getStatus())
                .certificationTypedate(airport.getCertificationTypedate())
                .customsAirportOfEntry(airport.getCustomsAirportOfEntry())
                .militaryJointUse(airport.getMilitaryJointUse())
                .militaryLanding(airport.getMilitaryLanding())
                .lightingSchedule(airport.getLightingSchedule())
                .beaconSchedule(airport.getBeaconSchedule())
                .controlTower(airport.getControlTower())
                .unicom(airport.getUnicom())
                .ctaf(airport.getCtaf())
                .effectiveDate(airport.getEffectiveDate())
                .build();
    }
}
