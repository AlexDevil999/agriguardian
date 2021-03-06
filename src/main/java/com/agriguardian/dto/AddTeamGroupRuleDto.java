package com.agriguardian.dto;

import com.agriguardian.domain.Point;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.enums.ZoneType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddTeamGroupRuleDto {

    @NotNull(message = "field 'teamGroupId' may not be null")
    private Long teamGroupId;
    @NotNull(message = "field 'type' may not be null")
    private ZoneType type;
    @NotNull(message = "field 'rule' may not be null")
    private ZoneRule rule;
    private String name;

    private Figure figureType;
    private Double centerLon;
    private Double centerLat;
    private Integer radius;
    private List<Point> borders;

    @NotEmpty(message = "field 'vulnerables' may not be empty")
    private Set<Long> vulnerables;
}
