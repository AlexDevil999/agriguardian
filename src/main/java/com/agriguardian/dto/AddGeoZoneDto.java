package com.agriguardian.dto;

import com.agriguardian.domain.Point;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.enums.ZoneType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.DayOfWeek;
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

    private ZoneRule zoneRule;
    private String timeZone;
    private DayOfWeek dayStart;
    private DayOfWeek dayEnd;

    @Pattern(regexp = "([012])[0-9]:[0-5][0-9]:[0-5][0-9]")
    private String timeStart;
    @Pattern(regexp = "([012])[0-9]:[0-5][0-9]:[0-5][0-9]")
    private String timeEnd;

    private Figure figureType;
    private Double centerLon;
    private Double centerLat;
    private Integer radius;
    private List<Point> borders;

    @NotEmpty(message = "field 'vulnerables' may not be empty")
    private Set<Long> vulnerables;
}
