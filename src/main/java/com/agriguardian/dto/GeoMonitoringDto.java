package com.agriguardian.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoMonitoringDto {
    @NotNull(message = "field 'locations' is mandatory")
    private List<LocationDto> locations;

    public LocationDto findLastLocation() {
        Collections.sort(locations);
        return locations.get(locations.size() - 1);
    }
}
