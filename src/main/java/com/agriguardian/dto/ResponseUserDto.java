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
    private Long id;
    private String username;
    private Long ownerOfGroup;
    private Map<Long, GroupRole> groups;
    private Status status;
    private UserRole userRole;
    private UserInfoDto userInfo;
    private Long createdOnMs;
    private SubscriptionDto subscription;


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
