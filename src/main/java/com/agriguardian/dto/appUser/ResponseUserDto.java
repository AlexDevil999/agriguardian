package com.agriguardian.dto.appUser;

import com.agriguardian.dto.SubscriptionDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.LocationData;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class ResponseUserDto {

    private Long id;
    private String username;
    private Long ownerOfGroup;
    private List<UserGroupBindDto> groups;
    private Status status;
    private UserRole userRole;
    private UserInfoDto userInfo;
    private Long createdOnMs;
    private SubscriptionDto subscription;
    private Integer userAvatar;
    private String macAddress;
    private LocationDataDto locationData;


    public static ResponseUserDto of(AppUser u) {
        if(u.getUserInfo()==null||u.getUserInfo().getUserAvatar()==null){
            return ResponseUserDto.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .subscription(SubscriptionDto.of(u.getSubscription()))
                    .ownerOfGroup(u.getOwnGroup())
                    .groups(defineTeamGroups(u))
                    .createdOnMs(u.getCreatedOnMs())
                    .status(u.getStatus())
                    .userRole(u.getUserRole())
                    .userInfo(UserInfoDto.of(u.getUserInfo()))
                    .locationData(LocationDataDto.of(u.getLocationData()))
                    .macAddress(u.getMacAddress())
                    .build();
        }
        return ResponseUserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .subscription(SubscriptionDto.of(u.getSubscription()))
                .ownerOfGroup(u.getOwnGroup())
                .groups(defineTeamGroups(u))
                .createdOnMs(u.getCreatedOnMs())
                .status(u.getStatus())
                .userRole(u.getUserRole())
                .userInfo(UserInfoDto.of(u.getUserInfo()))
                .locationData(LocationDataDto.of(u.getLocationData()))
                .userAvatar(u.getUserInfo().getUserAvatar())
                .macAddress(u.getMacAddress())
                .build();
    }

    private static List<UserGroupBindDto> defineTeamGroups(AppUser u) {
        return u.getAppUserTeamGroups() == null ? Collections.emptyList() :
                u.getAppUserTeamGroups()
                        .stream()
                        .map(ug -> {
                            TeamGroup g = ug.getTeamGroup();
                            return new UserGroupBindDto(g.getId(), g.getName(), u.getId(), ug.getGroupRole());
                        }).collect(Collectors.toList());
    }
}
