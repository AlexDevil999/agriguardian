package com.agriguardian.dto;

import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.ZoneRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ResponseAlertBluetoothZoneDto {
    private Long id;
    private Long associatedUser;
    private ZoneRule rule;
    private Long teamGroup;
    private Set<Long> vulnerables;


    public static ResponseAlertBluetoothZoneDto of(AlertBluetoothZone zone) {
        return ResponseAlertBluetoothZoneDto.builder()
                .id(zone.getId())
                .associatedUser(zone.getAssociatedUser().getId())
                .rule(zone.getRule())
                .teamGroup(zone.getTeamGroup().getId())
                .vulnerables(zone.extractVulnerables().stream().map(AppUser::getId).collect(Collectors.toSet()))
                .build();
    }
}
