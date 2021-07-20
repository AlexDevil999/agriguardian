package com.agriguardian.dto;

import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ResponseTeamGroupDto {
    private Long id;
    private String name;
    private String guardianInvitationCode;
    private String vulnerableInvitationCode;
    private ResponseUserDto owner;
    private Set<ResponseUserDto> participants;
    private Set<ResponseAlertBluetoothZoneDto> alertBluetoothZones;

    public static ResponseTeamGroupDto of(TeamGroup tg) {
        return ResponseTeamGroupDto.builder()
                .id(tg.getId())
                .name(tg.getName())
                .owner(ResponseUserDto.of(tg.getOwner()))
                .participants(tg.getAppUserTeamGroups().stream().map(AppUserTeamGroup::getAppUser).map(ResponseUserDto::of).collect(Collectors.toSet()))
                .alertBluetoothZones(tg.getAlertBluetoothZones().stream().map(ResponseAlertBluetoothZoneDto::of).collect(Collectors.toSet()))
                .guardianInvitationCode(tg.getGuardianInvitationCode())
                .vulnerableInvitationCode(tg.getVulnerableInvitationCode())
                .build();
    }
}
