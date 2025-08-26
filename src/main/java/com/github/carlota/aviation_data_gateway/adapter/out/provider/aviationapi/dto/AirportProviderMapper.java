package com.github.carlota.aviation_data_gateway.adapter.out.provider.aviationapi.dto;

import com.github.carlota.aviation_data_gateway.domain.model.Airport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AirportProviderMapper {

    public Map<String, List<Airport>> toDomainMap(Map<String, List<AviationApiDto>> dtoMap) {
        if (dtoMap == null || dtoMap.isEmpty()) {
            log.warn("Attempted to map empty/null provider response to domain model");
            return Collections.emptyMap();
        }

        String key = dtoMap.keySet().iterator().next();
        List<AviationApiDto> dtoList = dtoMap.get(key);
        log.debug("Mapping {} provider airport DTO(s) under key '{}' to domain", dtoList != null ? dtoList.size() : 0, key);

        List<Airport> domainList = dtoList.stream()
                .map(this::toDomain)
                .toList();

        return Collections.singletonMap(key, domainList);
    }

    private Airport toDomain(AviationApiDto dto) {
        return Airport.builder()
                .siteNumber(dto.getSiteNumber())
                .type(dto.getType())
                .facilityName(dto.getFacilityName())
                .faaIdent(dto.getFaaIdent())
                .icaoIdent(dto.getIcaoIdent())
                .region(dto.getRegion())
                .districtOffice(dto.getDistrictOffice())
                .state(dto.getState())
                .stateFull(dto.getStateFull())
                .county(dto.getCounty())
                .city(dto.getCity())
                .ownership(dto.getOwnership())
                .use(dto.getUse())
                .manager(dto.getManager())
                .managerPhone(dto.getManagerPhone())
                .latitude(dto.getLatitude())
                .latitudeSec(dto.getLatitudeSec())
                .longitude(dto.getLongitude())
                .longitudeSec(dto.getLongitudeSec())
                .elevation(dto.getElevation())
                .magneticVariation(dto.getMagneticVariation())
                .tpa(dto.getTpa())
                .vfrSectional(dto.getVfrSectional())
                .boundaryArtcc(dto.getBoundaryArtcc())
                .boundaryArtccName(dto.getBoundaryArtccName())
                .responsibleArtcc(dto.getResponsibleArtcc())
                .responsibleArtccName(dto.getResponsibleArtccName())
                .fssPhoneNumber(dto.getFssPhoneNumber())
                .fssPhoneNumerTollfree(dto.getFssPhoneNumerTollfree())
                .notamFacilityIdent(dto.getNotamFacilityIdent())
                .status(dto.getStatus())
                .certificationTypedate(dto.getCertificationTypedate())
                .customsAirportOfEntry(dto.getCustomsAirportOfEntry())
                .militaryJointUse(dto.getMilitaryJointUse())
                .militaryLanding(dto.getMilitaryLanding())
                .lightingSchedule(dto.getLightingSchedule())
                .beaconSchedule(dto.getBeaconSchedule())
                .controlTower(dto.getControlTower())
                .unicom(dto.getUnicom())
                .ctaf(dto.getCtaf())
                .effectiveDate(dto.getEffectiveDate())
                .build();
    }
}
