package com.agriguardian.dto.geoZone;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.ZoneSchedulingRule;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.enums.ZoneType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddGeoZoneDto {
    @NotNull(message = "field 'teamGroupId' may not be null")
    private Long teamGroupId;
    @NotNull(message = "field 'type' may not be null")
    private ZoneType type;
    @NotNull(message = "field 'rule' may not be null")
    private ZoneRule rule;
    private String name;

    private List<SchedulePeriodDto> schedulePeriodDtos;


    private Figure figureType;
    private Double centerLon;
    private Double centerLat;
    private Integer radius;
    private List<Point> borders;

    @NotEmpty(message = "field 'vulnerables' may not be empty")
    private Set<Long> vulnerables;

    public List<ZoneSchedulingRule> createZoneSchedulingRules(){

        if(schedulePeriodDtos==null)
            return null;

        List<ZoneSchedulingRule> zoneSchedulingRules = new ArrayList<>(schedulePeriodDtos.size());
        schedulePeriodDtos.forEach(schedulePeriodDto -> zoneSchedulingRules.add(schedulePeriodDto.createSchedulingRule()));
        return zoneSchedulingRules;
    }
}
