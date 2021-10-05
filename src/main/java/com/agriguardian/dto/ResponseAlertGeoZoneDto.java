package com.agriguardian.dto;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ResponseAlertGeoZoneDto {
    private Long id;
    private String name;
    private ZoneRule rule;
    private Long teamGroup;
    private Set<Long> vulnerables;


    private Figure figureType;
    private Double centerLon;
    private Double centerLat;
    private Integer radius;

    private List<Point> borders;


    public static ResponseAlertGeoZoneDto of(AlertGeoZone zone) {
        if(zone.getFigureType().equals(Figure.POLYGON)) {
            Collections.sort(zone.getBorders());
            return ResponseAlertGeoZoneDto.builder()
                    .id(zone.getId())
                    .name(zone.getName())
                    .rule(zone.getRule())
                    .teamGroup(zone.getTeamGroup().getId())
                    .vulnerables(zone.extractVulnerables().stream().map(AppUser::getId).collect(Collectors.toSet()))
                    .figureType(zone.getFigureType())
                    .centerLat(zone.getCenterLat())
                    .centerLon(zone.getCenterLon())
                    .radius(zone.getRadius())
                    .borders(zone.getBorders().stream().map(Point::of).collect(Collectors.toList()))
                    .build();
        }
        else{
            return ResponseAlertGeoZoneDto.builder()
                    .id(zone.getId())
                    .name(zone.getName())
                    .rule(zone.getRule())
                    .teamGroup(zone.getTeamGroup().getId())
                    .vulnerables(zone.extractVulnerables().stream().map(AppUser::getId).collect(Collectors.toSet()))
                    .figureType(zone.getFigureType())
                    .centerLat(zone.getCenterLat())
                    .centerLon(zone.getCenterLon())
                    .radius(zone.getRadius())
                    .build();
        }
    }
}
