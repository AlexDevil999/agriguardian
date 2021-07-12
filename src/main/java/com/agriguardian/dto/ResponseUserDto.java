package com.agriguardian.dto;

import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ResponseUserDto {
    private long id;
    private String username;
    private String name;
    private SubscriptionDto subscription;
    private Long ownerOfGroup;
    private Map<Long, GroupRole> groups;

    private long createdOnMs;

    private Status status;
    private UserRole userRole;

    private UserInfoDto userInfo;


    public static ResponseUserDto of(AppUser u) {
        return ResponseUserDto.builder()
        .id(u.getId())
        .username(u.getUsername())
        .subscription(SubscriptionDto.of(u.getSubscription()))
        .ownerOfGroup(u.getOwnGroup())
                .groups(u.defineTeamGroups())
        .createdOnMs(u.getCreatedOnMs())
        .status(u.getStatus())
        .userRole(u.getUserRole())
        .userInfo(UserInfoDto.of(u.getUserInfo()))
                .build();
    }
}
