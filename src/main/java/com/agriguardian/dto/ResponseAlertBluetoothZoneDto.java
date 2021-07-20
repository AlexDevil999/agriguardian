package com.agriguardian.dto;

import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.enums.ZoneRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ResponseAlertBluetoothZoneDto {
    private Long id;
    private ResponseUserDto associatedUser;
    private ZoneRule rule;
    private Long teamGroup;
    private Set<ResponseUserDto> vulnerables;


    public static ResponseAlertBluetoothZoneDto of(AlertBluetoothZone zone) {
        return ResponseAlertBluetoothZoneDto.builder()
                .id(zone.getId())
                .associatedUser(ResponseUserDto.of(zone.getAssociatedUser()))
                .rule(zone.getRule())
                .teamGroup(zone.getTeamGroup().getId())
                .vulnerables(zone.extractVulnerables().stream().map(ResponseUserDto::of).collect(Collectors.toSet()))
                .build();
    }
}
